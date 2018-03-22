package com.tbb.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 工具类--获取redis连接池
 *
 * @author: tubingbing
 * @Date: 2017/3/20
 * @Time: 13:13
 */
public class RedisClient {

    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    //private static ShardedJedisPool jedisPool;

    private static final String DEFAULT_IP = "127.0.0.1";

    private static final String DEFAULT_PORT = "6379";

    /*static {
        InputStream inputStream = null;
        try {
            inputStream = RedisClient.class.getResourceAsStream("/redis.properties");
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
            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxTotal(150);
            jedisPool = new ShardedJedisPool(config, shardInfoList, Hashing.MURMUR_HASH);
        } catch (Exception e) {
            logger.error("exception",e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                logger.error("io close exception",e);
            }
        }
    }*/

    /**
     * 获取连接
     *
     * @return
     */
    /*public static ShardedJedis getShardedJedis() {
        if (jedisPool == null) {
            return null;
        }
        return jedisPool.getResource();
    }*/

    /**
     * 释放连接
     *
     * @param jedis
     */
    /*public static void close(ShardedJedis jedis) {
        if (jedisPool == null) {
            return;
        }
        jedisPool.returnResource(jedis);
    }*/

}
