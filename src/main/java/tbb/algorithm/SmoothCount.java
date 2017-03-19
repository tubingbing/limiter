package tbb.algorithm;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: tubingbing
 * Date: 2017/3/19
 * Time: 13:36
 */
public class SmoothCount {

    private static LoadingCache<Long, ConcurrentMap<String, AtomicLong>> counter =
            CacheBuilder.newBuilder()
                    .expireAfterWrite(2, TimeUnit.SECONDS)
                    .build(new CacheLoader<Long, ConcurrentMap<String, AtomicLong>>() {
                        @Override
                        public ConcurrentMap<String, AtomicLong> load(Long aLong) throws Exception {
                            return new ConcurrentHashMap<String, AtomicLong>();
                        }
                    });

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
            e.printStackTrace();
        }
        return null;
    }

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


    public static boolean limiter(String key, long qps) {
        long currentSecond = System.currentTimeMillis() / 100; //每秒划分成10个窗口
        //获取当前窗口的计数
        AtomicLong count = getAtomicLong(key, currentSecond);
        //前9次窗口的总计数
        long totalCount = getTotalCount(key, currentSecond);
        if (totalCount + count.incrementAndGet() > qps) {
            return false;
        }
        return true;
    }
}
