package com.tbb.algorithm;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 令牌桶算法 （利用guava的ratelimiter）
 * User: tubingbing
 * Date: 2017/3/30
 * Time: 13:29
 */
public class TokenBucket {
    private static final Logger logger = LoggerFactory.getLogger(SmoothCount.class);

    private static final ConcurrentMap<String, RateLimiter> concurrentMap = new ConcurrentHashMap<String, RateLimiter>();

    /**
     * 通过key获取该接口的ratelimiter
     * @param key
     * @param qps
     * @return
     */
    private static RateLimiter getRateLimiter(String key, long qps) {
        RateLimiter limiter = concurrentMap.get(key);
        if (limiter == null) {
            limiter = RateLimiter.create(qps);
            RateLimiter oldLimiter = concurrentMap.putIfAbsent(key, limiter);
            if (oldLimiter != null) {
                limiter = oldLimiter;
            }
        }
        //qps变更需要
        limiter.setRate(qps);
        return limiter;
    }

    /**
     * 获取到token执行，未获取到返回false
     * @param key
     * @param qps
     * @return
     */
    public static boolean limiter(String key, long qps) {
        if ((key==null || key.equals("")) || qps <=0){
            return true;
        }
        RateLimiter limiter = getRateLimiter(key, qps);
        return limiter.tryAcquire();
    }

    /**
     * 所有请求在队列中等待执行等待获取token
     * @param key
     * @param qps
     * @return
     */
    public static boolean waitRequest(String key, long qps){
        if ((key==null || key.equals("")) || qps <=0){
            return true;
        }
        RateLimiter limiter = getRateLimiter(key, qps);
        limiter.acquire();
        return true;
    }
}
