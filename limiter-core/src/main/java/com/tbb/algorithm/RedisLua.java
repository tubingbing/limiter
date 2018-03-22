package com.tbb.algorithm;


/**
 * @author: tubingbing
 * @Date: 2017/3/30
 * @Time: 12:50
 */
public class RedisLua {
    //private static final Logger logger = LoggerFactory.getLogger(RedisLua.class);
    //lua脚本
    //private static String luaScript = "";

    /**
     * 读取本地文件获取lua脚本内容
     */
    /*static {
        InputStream inputStream = null;
        try {
            inputStream = RedisLua.class.getResourceAsStream("/limit.lua");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String str;
            while ((str = br.readLine()) != null) {
                luaScript += str + "\n";
            }
        } catch (Exception e) {
            logger.error("file read exception",e);
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
     * 限流方法
     *
     * @param key
     * @param qps
     * @return
     */
   /* public static boolean limiter(String key, long qps) {
        if ((key==null || key.equals("")) || qps <=0){
            return true;
        }
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
            logger.error("exception",e);
        } finally {
            if (shardedJedis != null) {
                RedisClient.close(shardedJedis);
            }
        }
        return true;
    }*/


}
