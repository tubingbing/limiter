package tbb.algorithm;

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
 * Time: 13:36
 */
public class SmoothCount {

    private static LoadingCache<Long, ConcurrentMap<String,AtomicLong>> counter =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(2, TimeUnit.SECONDS)
                    .build(new CacheLoader<Long, ConcurrentMap<String,AtomicLong>>() {
                        @Override
                        public ConcurrentMap<String,AtomicLong> load(Long aLong) throws Exception {
                            return new ConcurrentHashMap<String, AtomicLong>();
                        }
                    });

    public static boolean limiter(String key,long qps){
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
            if (count.incrementAndGet() > qps) {
                return false;
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

}