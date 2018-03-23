package com.tbb.controller;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

import java.util.ArrayList;
import java.util.List;


/**
 * 工具类--获取redis连接池
 *
 * @author: tubingbing
 * @Date: 2017/3/20
 * @Time: 13:13
 */
public class RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);
    
    private static ShardedJedisPool jedisPool;

    private static final String DEFAULT_IP = "127.0.0.1";

    private static final String DEFAULT_PORT = "6379";

    static {
        try {
            List<JedisShardInfo> shardInfoList = new ArrayList<JedisShardInfo>();
            for (int i = 0; i < 1; i++) {
                String ip = DEFAULT_IP;
                String port = DEFAULT_PORT;
                JedisShardInfo shardInfo = new JedisShardInfo(ip, Integer.parseInt(port));
                shardInfoList.add(shardInfo);
            }
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxTotal(150);
            jedisPool = new ShardedJedisPool(config, shardInfoList, Hashing.MURMUR_HASH);
        } catch (Exception e) {
            logger.error("exception",e);
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
