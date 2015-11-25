package com.lubby.task;

import com.lubby.cache.LocalCache;
import com.lubby.exception.LocalException;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by liubin on 2015-11-25.
 */
public class LocalCacheTask {

    public static void doTask(LocalCache localCache) {
        final LocalCache cache = localCache;
        Executor threadPool = Executors.newFixedThreadPool(2);
        /**
         * This is a back thread which cleans the expired value periodically
         */
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                cache.cleanExpiredValue();
            }
        });

        /**
         * This is a back thread whick saves the cache into a file periodically
         */
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LocalCache.save(cache);
                } catch (LocalException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
