package com.tbb.algorithm;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: tubingbing
 * Date: 2017/3/19
 * Time: 21:02
 */
public class SimpleCount {
    private static LoadingCache<Long, ConcurrentMap<String, AtomicLong>> counter =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(2, TimeUnit.SECONDS)
                    .build(new CacheLoader<Long, ConcurrentMap<String, AtomicLong>>() {
                        @Override
                        public ConcurrentMap<String, AtomicLong> load(Long aLong) throws Exception {
                            return new ConcurrentHashMap<String, AtomicLong>();
                        }
                    });

    private static AtomicLong getAtomicLong(String key) {
        try {
            long currentSecond = System.currentTimeMillis() / 1000;
            ConcurrentMap<String, AtomicLong> concurrentMap = counter.get(currentSecond);
            AtomicLong count = concurrentMap.get(key);
            if (count == null) {
                count = new AtomicLong(0);
                AtomicLong oldCount = concurrentMap.putIfAbsent(key, count);
                if (oldCount != null) {
                    count = oldCount;
                }
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean limiter(String key, long qps) {
        AtomicLong count = getAtomicLong(key);
        if (count == null) {
            return true;
        }
        if (count.incrementAndGet() > qps) {
            return false;
        }
        return true;
    }
}
