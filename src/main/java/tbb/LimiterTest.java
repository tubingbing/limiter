package tbb;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.*;

/**
 * User: tubingbing
 * Date: 2017/3/17
 * Time: 21:55
 */
public class LimiterTest {

    public static final ConcurrentMap<String,RateLimiter> concurrentMap = new ConcurrentHashMap<String, RateLimiter>();

    public static boolean limiter(String key,long qps){
        RateLimiter limiter = concurrentMap.get(key);
        if (limiter==null){
            limiter = RateLimiter.create(qps);
            RateLimiter oldLimiter = concurrentMap.putIfAbsent(key,limiter);
            if (oldLimiter != null) {
                limiter = oldLimiter;
            }
        }
        //limiter.acquire();
        return limiter.tryAcquire();
    }
}

