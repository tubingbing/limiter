package com.tbb.redis;

import com.tbb.algorithm.RedisLua;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 工具类--获取redis连接池
 *
 * @author: tubingbing
 * @Date: 2017/3/20
 * @Time: 13:13
 */
public class RedisClient {

    private static ShardedJedisPool jedisPool;

    private static final String DEFAULT_IP = "127.0.0.1";

    private static final String DEFAULT_PORT = "6379";

    static {
        try {
            String path = RedisLua.class.getResource("/").getPath();
            FileInputStream inputStream = new FileInputStream(new File(path + "redis.properties"));
            Properties properties = new Properties();
            properties.load(inputStream);
            String[] ips = properties.getProperty("redis.ip", DEFAULT_IP).split(",");
            String[] ports = properties.getProperty("redis.port", DEFAULT_PORT).split(",");
            List<JedisShardInfo> shardInfoList = new ArrayList<JedisShardInfo>();
            int length = Math.max(ips.length, ports.length);
            for (int i = 0; i < length; i++) {
                String ip = ips[i];
                String port = ports[i];
                if (ip == null) {
                    ip = DEFAULT_IP;
                }
                if (port == null) {
                    port = DEFAULT_PORT;
                }
                JedisShardInfo shardInfo = new JedisShardInfo(ip, Integer.parseInt(port));
                shardInfoList.add(shardInfo);
            }
            jedisPool = new ShardedJedisPool(new GenericObjectPoolConfig(), shardInfoList, Hashing.MURMUR_HASH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     *
     * @return
     */
    public static ShardedJedis getShardedJedis() {
        if (jedisPool == null) {
            return null;
        }
        return jedisPool.getResource();
    }

    /**
     * 释放连接
     *
     * @param jedis
     */
    public static void close(ShardedJedis jedis) {
        if (jedisPool == null) {
            return;
        }
        jedisPool.returnResource(jedis);
    }

}
