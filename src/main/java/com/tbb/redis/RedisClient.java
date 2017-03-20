package com.tbb.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: tubingbing
 * @Date: 2017/3/20
 * @Time: 13:13
 */
public class RedisClient {

    private static ShardedJedisPool jedisPool;

    static {
        List<JedisShardInfo> shardInfoList = new ArrayList<JedisShardInfo>();
        JedisShardInfo hostAndPort = new JedisShardInfo("127.0.0.1",6379);
        shardInfoList.add(hostAndPort);
        jedisPool = new ShardedJedisPool(new GenericObjectPoolConfig(),shardInfoList);
    }

    public static ShardedJedis getShardedJedis(){
        return jedisPool.getResource();
    }

    


}
