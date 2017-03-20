package com.tbb.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类--获取redis连接池
 * @author: tubingbing
 * @Date: 2017/3/20
 * @Time: 13:13
 */
public class RedisClient {

    private static ShardedJedisPool jedisPool;

    static {
        List<JedisShardInfo> shardInfoList = new ArrayList<JedisShardInfo>();
        for(int i=0;i<10;i++){
            JedisShardInfo hostAndPort = new JedisShardInfo("127.0.0.1",6379);
            shardInfoList.add(hostAndPort);
        }
        jedisPool = new ShardedJedisPool(new GenericObjectPoolConfig(),shardInfoList, Hashing.MURMUR_HASH);
    }

    /**
     * 获取连接
     * @return
     */
    public static ShardedJedis getShardedJedis(){
        return jedisPool.getResource();
    }

    /**
     * 释放连接
     * @param jedis
     */
    public static void close(ShardedJedis jedis){
        jedisPool.returnResource(jedis);
    }

}
