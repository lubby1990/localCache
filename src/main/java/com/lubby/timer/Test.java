package com.lubby.timer;

import com.google.gson.reflect.TypeToken;
import com.lubby.cache.LocalCache;
import com.lubby.exception.LocalException;
import com.lubby.task.CleanExpiredValueTask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by liubin on 2015-11-24.
 */
public class Test {
    public static void main(String[] args) {
//        final CleanExpiredValueTask task = new CleanExpiredValueTask();
//        LocalCache<String, String> localCache = new LocalCache<String, String>();

//        Executor threadPool = Executors.newFixedThreadPool(1);
//        for (int i = 0; i < 1000; i++) {
//            localCache.setValue(String.valueOf(i), "value" + i, TimeUnit.SECONDS, i % 2 * 20 + 10);
//        }
//
//        for (int i = 0; i < 1000; i++) {
//            String value = localCache.getValue(String.valueOf(i));
//            System.out.println(value);
//        }

    /*    task.localCache = localCache;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task.cleanExpiredValue();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, new Date(), 10 * 1000);*/
//        try {
//            TimeUnit.SECONDS.sleep(30);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


//        for (int i = 0; i < 1000; i++) {
//            String value = localCache.getValue(String.valueOf(i));
//            System.out.println(value);
//        }


        LocalCache<String, String> localCache = new LocalCache<String, String>();
        for (int i = 0; i < 10; i++) {
            localCache.setValue(String.valueOf(i), "value" + i);
        }
        try {
            LocalCache.save(localCache);

            localCache = null;
            Type type = new TypeToken<LocalCache<String, String>>(){}.getType();
            localCache = LocalCache.read(type);
        } catch (LocalException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 10; i++) {
            System.out.println( localCache.getValue(String.valueOf(i)));
        }
    }
}
