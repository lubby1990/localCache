package com.lubby.cache;

import com.google.gson.Gson;
import com.lubby.exception.LocalException;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by liubin on 2015-11-23.
 * localCache
 */
public class LocalCache<K, V> implements Serializable {
    private static final long serialVersionUID = 4742575543754467445L;
    //path of store path
    private final static String STORE_FILE_PATH = "D:\\localCache";
    //name of store file name
    private final static String STORE_FILE_NAME = "localCache.txt";
    //the max size of cache
    private int MAX_SIZE = 10000;
    //the cache core
    private Map<K, ValueEntry<V>> cache;

    /**
     * the construct of local cache with no param
     */
    public LocalCache() {
        cache = new HashMap<>();
    }

    /**
     * the construct of local cache
     *
     * @param initialCapacity the initialCapacity for cache
     */
    public LocalCache(int initialCapacity) {
        cache = new HashMap<>(initialCapacity);
    }

    /**
     * the construct of local cache
     *
     * @param initialCapacity initial capacity - MUST be a power of two.
     * @param loadFactor      The load factor for the hash table.
     * @param maxSize         the max size of cache
     * @param storeFilePath   the path of store file
     * @throws LocalException if the store file path does not exist throw LocalException
     */
    public LocalCache(int initialCapacity, int loadFactor, int maxSize, String storeFilePath) throws LocalException {
        cache = new HashMap<>(initialCapacity, loadFactor);
        MAX_SIZE = maxSize;
        File file = new File(storeFilePath);
        if (!file.exists()) {
            throw new LocalException("store file does not exsit. the storeFilePath is " + storeFilePath);
        }
    }

    /**
     * Sets the max size of cache
     */
    public void setMaxSize(int size) {
        this.MAX_SIZE = size;
    }

    /**
     * Gets the max size of cache
     */
    public int setMaxSize() {
        return this.MAX_SIZE;
    }


    /**
     * Get size of cache
     *
     * @return the number of key-value mappings in  cache
     */
    public int size() {
        return cache.size();
    }

    /**
     * Get value from cache
     *
     * @param key the key of value
     * @return V value in the cache .
     */
    public V getValue(K key) {
        ValueEntry<V> valueEntry = cache.get(key);
        if (valueEntry.getExpiredTime() == -1 || valueEntry.getExpiredTime() > System.currentTimeMillis() / 1000) {
            return valueEntry.getValue();
        }
        return null;
    }

    /**
     * Puts value into Cache.
     *
     * @param value the value you want put into cache
     * @return if cache exists key then return old value, else return null
     */
    public V setValue(K key, V value) {
        //check if cache more than MAX_SIZE
        if (cache.size() >= MAX_SIZE) {
            return null;
        }
        ValueEntry<V> valueEntry = new ValueEntry<>();
        valueEntry.setValue(value);
        ValueEntry<V> oldValueEntry = cache.put(key, valueEntry);
        return oldValueEntry == null ? null : oldValueEntry.getValue();
    }

    /**
     * Puts value into Cache with expired time.
     *
     * @param value       the value you want put into cache
     * @param expiredDate the expired date of value
     * @return if cache exists key then return old value, else return null
     */
    public V setValue(K key, V value, Date expiredDate) {
        //check if cache more than MAX_SIZE
        if (cache.size() >= MAX_SIZE) {
            return null;
        }
        ValueEntry<V> valueEntry = new ValueEntry<>();
        valueEntry.setExpiredTime(expiredDate.getTime() / 1000);
        valueEntry.setValue(value);
        ValueEntry<V> oldValueEntry = cache.put(key, valueEntry);
        return oldValueEntry == null ? null : oldValueEntry.getValue();
    }

    /**
     * Puts value into Cache with expired time.
     *
     * @param value       the value you want put into cache
     * @param timeUnit    the time unit for expired time
     * @param expiredTime the expired time of value
     * @return if cache exists key then return old value, else return null
     */
    public V setValue(K key, V value, TimeUnit timeUnit, long expiredTime) {
        ValueEntry<V> valueEntry = new ValueEntry<>();
        valueEntry.setValue(value);
        long expiredSeconds = System.currentTimeMillis() / 1000 + timeUnit.toSeconds(expiredTime);
        valueEntry.setExpiredTime(expiredSeconds);
        ValueEntry<V> oldValueEntry = cache.put(key, valueEntry);
        return oldValueEntry == null ? null : oldValueEntry.getValue();
    }

    /**
     * Sets expired seconds for value in cache
     *
     * @param key            the key of cache
     * @param expiredSeconds the expired seconds
     */
    public void setExpiredTime(K key, long expiredSeconds) throws LocalException {
        setExpiredTime(key, TimeUnit.SECONDS, expiredSeconds);
    }

    /**
     * Sets expired date for value in cache with
     *
     * @param key         the key of cache
     * @param expiredTime the expired time of value
     * @throws LocalException if value does not exist in cache
     */
    public void setExpiredDate(K key, Date expiredTime) throws LocalException {
        long expiredSeconds = expiredTime.getTime() / 1000;
        setExpiredTime(key, TimeUnit.SECONDS, expiredSeconds);
    }

    /**
     * Sets expired time for value in cache
     *
     * @param key         the key of cache
     * @param timeUnit    the time unit for expired time
     * @param expiredTime the expired time of value
     */
    public void setExpiredTime(K key, TimeUnit timeUnit, long expiredTime) throws LocalException {
        ValueEntry<V> valueEntry = cache.get(key);
        long expiredCurrentSeconds = System.currentTimeMillis() / 1000 + expiredTime;
        if (valueEntry.getExpiredTime() > expiredCurrentSeconds) {
            throw new LocalException("key does not exist in cache");
        }
        valueEntry.setExpiredTime(expiredCurrentSeconds);
        cache.put(key, valueEntry);
    }

    /**
     * Cleaning expired value in cache
     */
    public void cleanExpiredValue() {
        Iterator iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ValueEntry<V>> entry = (Map.Entry<String, ValueEntry<V>>) iterator.next();
            ValueEntry<V> valueEntry = entry.getValue();
            if (valueEntry.getExpiredTime() <= System.currentTimeMillis() / 1000) {
                iterator.remove();
            }
        }
        System.out.println("size : " + cache.size());
    }

    /**
     * Save into temp file
     */
    public static boolean save(LocalCache cache) throws LocalException, IOException {
        Gson gson = new Gson();
        String storeFile = STORE_FILE_PATH + "/" + STORE_FILE_NAME;
        File file = new File(storeFile);
        FileOutputStream fos = new FileOutputStream(file);
        String json = gson.toJson(cache, LocalCache.class);
        byte[] data = json.getBytes("UTF-8");
        fos.write(data);
        fos.close();
        return true;
    }

    /**
     * Reads from file
     */
    public static LocalCache read(Type type) throws IOException {
        Gson gson = new Gson();
        String storeFile = STORE_FILE_PATH + "/" +STORE_FILE_NAME;
        File file = new File(storeFile);
        Reader reader = new InputStreamReader(new FileInputStream(file));
        String json = "";
        int ch;

        while ((ch = reader.read()) != -1) {
            json = json + (char) ch;
        }
        reader.close();
        return gson.fromJson(json, type);
    }


    /**
     * The Value Struct contains expiredTime and value
     */
    static class ValueEntry<V> {
        private long expiredTime;
        private V value;

        public ValueEntry() {
            this.expiredTime = -1;
        }

        public ValueEntry(long expiredTime) {
            this.expiredTime = expiredTime;
        }

        public long getExpiredTime() {
            return expiredTime;
        }

        public void setExpiredTime(long expiredTime) {
            this.expiredTime = expiredTime;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }
}
