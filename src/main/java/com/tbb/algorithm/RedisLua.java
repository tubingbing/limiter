package com.tbb.algorithm;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.tbb.redis.RedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author: tubingbing
 * @Date: 2017/3/20
 * @Time: 12:50
 */
public class RedisLua {

    private static  String luaScript = null;

    static{
        try {
            String path = RedisLua.class.getResource("/").getPath();
            luaScript = Files.toString(new File(path + "limit.lua"), Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 限流方法
     *
     * @param key
     * @param qps
     * @return
     */
    public static boolean limiter(String key, long qps) {
        ShardedJedis shardedJedis = null;
        try {
            if (luaScript != null) {
                shardedJedis = RedisClient.getShardedJedis();
                String keys = key + "_" + System.currentTimeMillis() / 1000;//此处将当前时间戳取秒数
                Jedis jedis = shardedJedis.getShard(keys);
                String limit = String.valueOf(qps);//限流大小
                boolean flag = (Long) jedis.eval(luaScript, Lists.newArrayList(keys), Lists.newArrayList(limit)) == 1;
                return flag;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (shardedJedis != null) {
                RedisClient.close(shardedJedis);
            }
        }
        return true;
    }


}
