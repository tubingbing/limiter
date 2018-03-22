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
 * 平滑窗口计数
 * User: tubingbing
 * Date: 2017/3/30
 * Time: 13:36
 */
public class SmoothCount {
    private static final Logger logger = LoggerFactory.getLogger(SmoothCount.class);

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
    private static AtomicLong getAtomicLong(String key, long currentSecond) {
        try {
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
            logger.error("exception",e);
            //e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取前9次窗口的总限流数
     * @param key
     * @param currentSecond
     * @return
     */
    private static long getTotalCount(String key, long currentSecond) {
        long qps = 0;
        for (int i = 1; i < 10; i++) {
            AtomicLong count = getAtomicLong(key, currentSecond - i);
            if (count != null) {
                qps += count.get();
            }
        }
        return qps;
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
        long currentSecond = System.currentTimeMillis() / 100; //每秒划分成10个窗口
        //获取当前窗口的计数
        AtomicLong count = getAtomicLong(key, currentSecond);
        //前9次窗口的总计数
        long totalCount = getTotalCount(key, currentSecond);
        if (totalCount + count.incrementAndGet() > qps) {
            count.decrementAndGet(); //未通过的请求计数-1
            return false;
        }
        return true;
    }
}
