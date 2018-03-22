package com.tbb.algorithm;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 简单计数
 * User: tubingbing
 * Date: 2017/3/30
 * Time: 21:02
 */
public class SimpleCount {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCount.class);
    //构建guava本地缓存
    private static LoadingCache<Long, ConcurrentMap<String, AtomicLong>> counter =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(2, TimeUnit.SECONDS)
                    .build(new CacheLoader<Long, ConcurrentMap<String, AtomicLong>>() {
                        @Override
                        public ConcurrentMap<String, AtomicLong> load(Long aLong) throws Exception {
                            return new ConcurrentHashMap<String, AtomicLong>();
                        }
                    });

    /**
     * 通过当前时间获取对应的接口限流数
     * @param key
     * @return
     */
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
            logger.error("simpleCount exception",e);
        }
        return null;
    }


    /**
     * 限流方法
     * @param key
     * @param qps
     * @return
     */
    public static boolean limiter(String key, long qps) {
        if ((key==null || key.equals("")) || qps <=0){
            return true;
        }
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
