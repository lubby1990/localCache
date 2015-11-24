package com.lubby.task;

import com.lubby.cache.LocalCache;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by liubin on 2015-11-23.
 * The Task for cleaning expired value in cache
 */
public class CleanExpiredValueTask {

    @Resource
    public LocalCache localCache;



    /**
     * this is the task to cleaning expired value in cache
     */
    public void cleanExpiredValue() {
        localCache.cleanExpiredValue();
    }
}
