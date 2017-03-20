package com.tbb.algorithm;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author: tubingbing
 * @Date: 2017/3/20
 * @Time: 12:50
 */
public class RedisLua {

    public static boolean limiter(String key,long qps){
        try {
            String path = RedisLua.class.getResource("/").getPath();
            String luaScript = Files.toString(new File(path+"limit.lua"), Charset.defaultCharset());
            Jedis jedis = new Jedis("127.0.0.1", 6379);
            String keys = key + ":" + System.currentTimeMillis() / 1000;//此处将当前时间戳取秒数
            String limit = String.valueOf(qps);//限流大小
            boolean flag = (Long) jedis.eval(luaScript, Lists.newArrayList(keys), Lists.newArrayList(limit)) == 1;
            jedis.close();
            return flag;
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }
}
