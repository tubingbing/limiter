package com.tbb.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 漏桶实现
 * User: tubingbing
 * Date: 2017/3/30
 * Time: 22:42
 */
public class LeakyBucket {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(LeakyBucket.class);
    //所有限流接口map
    private static final ConcurrentMap<String, Leaky> concurrentMap = new ConcurrentHashMap<String, Leaky>();

    /**
     * 得到对应限流接口的漏桶类
     * @param key
     * @param qps
     * @return
     */
    private static Leaky getLeaky(String key, long qps) {
        Leaky leaky = concurrentMap.get(key);
        if (leaky == null) {
            leaky = new Leaky(qps);
            Leaky oldLeaky = concurrentMap.putIfAbsent(key, leaky);
            if (oldLeaky != null) {
                leaky = oldLeaky;
            }
        }
        leaky.setRate(qps);
        return leaky;
    }

    /**
     * 限流
     * @param key 接口
     * @param qps 总数
     * @return  
     */
    public static boolean limiter(String key, long qps) {
        if ((key==null || key.equals("")) || qps <=0){
            return true;
        }
        Leaky leaky = getLeaky(key, qps);
        if (leaky != null) {
            return leaky.grant();
        }
        return true;
    }

    /**
     * 内部静态漏桶类
     */
    static class Leaky extends ReentrantLock {
        private long oldSecond = System.currentTimeMillis() / 1000;
        private long capacity; // 桶的容量
        private long rate; // 水漏出的速度
        private long water; // 当前水量(当前累积请求数)

        Leaky(long qps) {
            setRate(qps);
        }

        private void setRate(long qps){
            this.rate = qps;
            this.capacity=qps;
        }

        /**
         * 请求是否允许通过
         * @return
         */
        private boolean grant() {
            this.lock();
            try {
                long now = System.currentTimeMillis() / 1000;
                water = Math.max(0, water - (now - oldSecond) * rate); // 先执行漏水，计算剩余水量
                oldSecond = now;
                if ((water + 1) <= capacity) {// 尝试加水,并且水还未满
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
