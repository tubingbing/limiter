package com.tbb.algorithm;

/**
 * User: tubingbing
 * Date: 2017/3/19
 * Time: 22:42
 */
public class LeakyBucket {
    public static boolean limiter(String key, long qps) {

       /* public class LeakyDemo {
            public long timeStamp = getNowTime();
            public int capacity; // 桶的容量
            public int rate; // 水漏出的速度
            public int water; // 当前水量(当前累积请求数)
            public boolean grant() {
                long now = getNowTime();
                water = max(0, water - (now - timeStamp) * rate); // 先执行漏水，计算剩余水量
                timeStamp = now;
                if ((water + 1) < capacity) {
// 尝试加水,并且水还未满
                    water += 1;
                    return true;
                }
                else {
// 水满，拒绝加水
                    return false;
                }
            }
        }*/

        return true;
    }
}
