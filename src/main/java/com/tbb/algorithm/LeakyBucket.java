package com.tbb.algorithm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 漏桶实现
 * User: tubingbing
 * Date: 2017/3/19
 * Time: 22:42
 */
public class LeakyBucket {

    public static final ConcurrentMap<String, Leaky> concurrentMap = new ConcurrentHashMap<String, Leaky>();

    private static Leaky getLeaky(String key, long qps) {
        Leaky leaky = concurrentMap.get(key);
        if (leaky == null) {
            leaky = new Leaky(qps);
            Leaky oldLeaky = concurrentMap.putIfAbsent(key, leaky);
            if (oldLeaky != null) {
                leaky = oldLeaky;
            }
        }
        return leaky;
    }

    public static boolean limiter(String key, long qps) {
        Leaky leaky = getLeaky(key, qps);
        if (leaky != null) {
            return leaky.grant();
        }
        return true;
    }

    static class Leaky extends ReentrantLock {
        public long oldSecond = System.currentTimeMillis() / 1000;
        public long capacity; // 桶的容量
        public long rate; // 水漏出的速度
        public long water; // 当前水量(当前累积请求数)

        Leaky(long qps) {
            this.capacity = qps;
            this.rate = qps;
        }

        public boolean grant() {
            this.lock();
            try {
                long now = System.currentTimeMillis() / 1000;
                water = Math.max(0, water - (now - oldSecond) * rate); // 先执行漏水，计算剩余水量
                oldSecond = now;
                if ((water + 1) < capacity) {// 尝试加水,并且水还未满
                    water += 1;
                    return true;
                } else {// 水满，拒绝加水
                    return false;
                }
            } finally {
                this.unlock();
            }
        }
    }
}
