package com.tbb.algorithm;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.tbb.redis.RedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: tubingbing
 * @Date: 2017/3/20
 * @Time: 12:50
 */
public class RedisLua {

    private static final String LUA_SCRIPT_KEY = "LUA_SCRIPT_KEY";

    private static final ConcurrentMap<String, String> concurrentMap = new ConcurrentHashMap<String, String>(2);

    /**
     * 只获取一次lua脚本，以后从本地缓存中获取
     *
     * @return
     */
    private static String getLuaScript() {
        try {
            String luaScript = concurrentMap.get(LUA_SCRIPT_KEY);
            if (luaScript == null) {
                String path = RedisLua.class.getResource("/").getPath();
                luaScript = Files.toString(new File(path + "limit.lua"), Charset.defaultCharset());
                String oldLuaScript = concurrentMap.putIfAbsent(LUA_SCRIPT_KEY, luaScript);
                if (oldLuaScript != null) {
                    luaScript = oldLuaScript;
                }
            }
            return luaScript;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            String luaScript = getLuaScript();
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
