package com.tbb.algorithm;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 令牌桶算法 （利用guava的ratelimiter）
 * User: tubingbing
 * Date: 2017/3/19
 * Time: 13:29
 */
public class TokenBucket {

    public static final ConcurrentMap<String, RateLimiter> concurrentMap = new ConcurrentHashMap<String, RateLimiter>();

    private static RateLimiter getRateLimiter(String key, long qps) {
        RateLimiter limiter = concurrentMap.get(key);
        if (limiter == null) {
            limiter = RateLimiter.create(qps);
            RateLimiter oldLimiter = concurrentMap.putIfAbsent(key, limiter);
            if (oldLimiter != null) {
                limiter = oldLimiter;
            }
        }
        return limiter;
    }

    public static boolean limiter(String key, long qps) {
        RateLimiter limiter = getRateLimiter(key, qps);
        //limiter.acquire();
        return limiter.tryAcquire();
    }
}
