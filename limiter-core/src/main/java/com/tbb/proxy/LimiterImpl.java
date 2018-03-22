package com.tbb.proxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tbb.algorithm.LeakyBucket;
import com.tbb.algorithm.SimpleCount;
import com.tbb.algorithm.SmoothCount;
import com.tbb.algorithm.TokenBucket;
import com.tbb.constant.LimiterEnum;
import com.tbb.domain.ClientDomain;
import com.tbb.domain.LimiterDomain;
import com.tbb.util.JsonUtil;
import com.tbb.util.StringUtil;
import com.tbb.zk.ZKCacheClient;
import com.tbb.zk.ZKCacheOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 通过代码硬编码限流
 *
 * @author: tubingbing
 * @Date: 2017/5/19
 * @Time: 9:38
 */
public class LimiterImpl {
    private static final Logger logger = LoggerFactory.getLogger(LimiterImpl.class);
    private static String basePath = "/o2olimiter";
    //构建guava本地缓存
    private static LoadingCache<String, Boolean> rejectMap = null;

    private static ConcurrentMap<String, Integer> timeMaps = new ConcurrentHashMap<String, Integer>();

    private static ZKCacheOperations zkCacheOperations;

    private static String systemId;

    private LimiterImpl() {
    }

    public static void init(String index, String systemId) {
        LimiterImpl.systemId = systemId;
        if (zkCacheOperations == null) {
            ZKCacheClient client = new ZKCacheClient();
            if (index == null || index.equals("")) {
                return;
            }
            if (index.contains(":")) {
                client.setUrl(index);
            } else {
                client.setIndex(index);
            }
            client.init();
            zkCacheOperations = new ZKCacheOperations();
            zkCacheOperations.setZKCacheClient(client);
            logger.info("limiter init success");
        }
    }

    /**
     * 限流方法，不包括防刷验证
     *
     * @param path
     * @param type
     * @return
     */
    public static boolean limiter(String path, LimiterEnum type) {
        String realPath = transToZKPath(path);
        LimiterDomain domain = getInfoByZK(realPath);
        if (domain != null && domain.getOpenLimiter() != null && domain.getOpenLimiter() == 1 && domain.getTotalQps() != null) {
            boolean o = grant(type, domain.getTotalQps(), realPath, null);
            return !o;
        }
        return false;
    }

    /**
     * 限流方法
     *
     * @param path
     * @param type
     * @param parameters
     * @return false:通过 true：被限流
     * @throws Exception
     */
    public static boolean limiter(String path, LimiterEnum type, Object[] parameters) {
        String realPath = transToZKPath(path);
        LimiterDomain domain = getInfoByZK(realPath);
        if (domain == null) {
            return false;
        }
        if (domain.getOpenBrush() != null && domain.getOpenBrush() == 1) { //1：开启防刷
            ClientDomain clientDomain = getClientDomain(parameters);
            if (domain.getRequestQps() != null && domain.getRequestQps() > 0 && clientDomain != null && domain.getRequestType() != null) {
                initRejectMap(realPath, domain);//创建本地缓存map
                String paramtersPath = getParamtersPath(realPath, domain, clientDomain); //获取防刷的路径
                Boolean bol = true;
                try {
                    bol = rejectMap.get(paramtersPath);
                } catch (ExecutionException e) {
                }
                if (!bol) {  //判断是否在限制时长内
                    return true;
                }
                //防刷限制
                boolean o = grant(type, domain.getRequestQps(), paramtersPath, parameters);
                if (!o) { //达到防刷限制次数，放入本地缓存
                    rejectMap.put(paramtersPath, false);
                    return true;
                }
            }
        }
        if (domain.getOpenLimiter() != null && domain.getOpenLimiter() == 1 && domain.getTotalQps() != null) {  //1：开启限流
            //接口是否限制
            boolean o = grant(type, domain.getTotalQps(), realPath, parameters);
            return !o;
        }
        return false;
    }

    /**
     * 初始化rejectmap（通过参数pin，设备号，ip等进行防刷）
     * 根据配置的限制时长 动态的创建拒绝访问的本地map
     *
     * @param realPath
     * @param domain
     */
    private static void initRejectMap(String realPath, LimiterDomain domain) {
        Integer oldTime = timeMaps.putIfAbsent(realPath, domain.getTime());
        if (oldTime == null) {
            oldTime = domain.getTime();
        }
        if (rejectMap == null || oldTime != domain.getTime()) {
            synchronized (LimiterImpl.class) {
                oldTime = timeMaps.putIfAbsent(realPath, domain.getTime());
                if (oldTime == null) {
                    oldTime = domain.getTime();
                }
                if (rejectMap == null || oldTime != domain.getTime()) {
                    timeMaps.put(realPath, domain.getTime());
                    rejectMap = CacheBuilder.newBuilder()
                            .expireAfterWrite(domain.getTime(), TimeUnit.MINUTES)
                            .build(new CacheLoader<String, Boolean>() {
                                @Override
                                public Boolean load(String key) throws Exception {
                                    return true;
                                }
                            });
                }
            }
        }
    }

    /**
     * （通过参数pin，设备号，ip等进行防刷）
     * 得到防刷的路径
     *
     * @param realPath
     * @param domain
     * @param clientDomain
     * @return
     */
    private static String getParamtersPath(String realPath, LimiterDomain domain, ClientDomain clientDomain) {
        StringBuilder sb = new StringBuilder();
        sb.append(realPath);
        //1:pin 2：ip 3:deviceId 4:platcode
        for (int k = 0; k < domain.getRequestType().length; k++) {
            if (domain.getRequestType()[k] == 1) {
                sb.append("_pin:" + (clientDomain.getPin() == null ? "" : clientDomain.getPin()));
            }
            if (domain.getRequestType()[k] == 2) {
                sb.append("_ip:" + (clientDomain.getIp() == null ? "" : clientDomain.getIp()));
            }
            if (domain.getRequestType()[k] == 3) {
                sb.append("_deviceId:" + (clientDomain.getDeviceId() == null ? "" : clientDomain.getDeviceId()));
            }
            if (domain.getRequestType()[k] == 4) {
                sb.append("_platCode:" + (clientDomain.getPlatCode() == null ? "" : clientDomain.getPlatCode()));
            }
        }
        return sb.toString();
    }

    /**
     * 获取zk节点内容
     *
     * @param path
     * @return
     */
    private static LimiterDomain getInfoByZK(String path) {
        LimiterDomain domain = null;
        try {
            if (zkCacheOperations == null) {
                return null;
            }
            String json = String.valueOf(zkCacheOperations.get(path));
            domain = JsonUtil.toObj(json, LimiterDomain.class);
            logger.debug("limiter:{}", domain);
        } catch (Exception e) {
            logger.error("zk异常", e);
        }
        return domain;
    }

    /**
     * 路径转换
     *
     * @param value
     * @return
     */
    private static String transToZKPath(String value) {
        StringBuilder sb = new StringBuilder(basePath);
        if (StringUtil.isNotEmpty(systemId)) {
            if (!systemId.startsWith("/")) {
                sb.append("/");
            }
            sb.append(systemId);
        }
        if (StringUtil.isNotEmpty(value)) {
            if (!value.startsWith("/")) {
                sb.append("/");
            }
            sb.append(value);
            if (value.endsWith("/")) {
                return sb.substring(0, sb.length() - 1);
            }
        }
        return sb.toString();
    }

    /**
     * 是否允许通过
     *
     * @param type
     * @param qps
     * @param path
     * @return true通过 false 限制
     * @throws Exception
     */
    private static boolean grant(LimiterEnum type, long qps, String path, Object[] parameters) {
        boolean flag;
        switch (type) {
            case SIMPLE_COUNT:  //简单计数
                flag = SimpleCount.limiter(path, qps);
                break;
            case SMOOTH_COUNT:  //平滑窗口计数
                flag = SmoothCount.limiter(path, qps);
                break;
            case LEAKY_BUCKET: //漏桶
                flag = LeakyBucket.limiter(path, qps);
                break;
            case TOKEN_BUCKET_NOTWAIT: //令牌桶 有token执行，未获取到返回false
                flag = TokenBucket.limiter(path, qps);
                break;
            case TOKEN_BUCKET_WAIT: //默认令牌桶 其他请求等待获取token,不拒绝请求
                flag = TokenBucket.waitRequest(path, qps);
                break;
            default: //默认令牌桶 其他请求等待获取token,不拒绝请求
                flag = TokenBucket.waitRequest(path, qps);
                break;
        }
        if (!flag) {
            logger.error("limiter 请求过于频繁，请稍后再试 参数：" + JsonUtil.toStr(parameters));
        }
        return flag;
    }

    /**
     * 获取用户信息
     *
     * @param parameters
     * @return
     * @throws Exception
     */
    private static ClientDomain getClientDomain(Object[] parameters) {
        for (int i = 0; i < parameters.length; i++) {
            Object obj = parameters[i];
            if (obj instanceof String || obj instanceof Integer || obj instanceof Long || obj instanceof Short || obj instanceof Byte
                    || obj instanceof Character || obj instanceof Double || obj instanceof Float || obj instanceof Void || obj instanceof Boolean) {
                continue;
            }
            String str = JsonUtil.toStr(obj);
            if (str != null && !"".equals(str)) {
                try {
                    ClientDomain client = JsonUtil.toObj(str, new TypeReference<ClientDomain>() {
                    });
                    //如果4个维度中有一个不为空，则退出参数解析循环
                    if (client != null && (client.getPin() != null || client.getDeviceId() != null || client.getIp() != null || client.getPlatCode() != null)) {
                        return client;
                    }
                } catch (Exception e) {
                }
            }
        }
        return null;

    }

    public static void setZKCacheOperations(ZKCacheOperations zkCacheOperations) {
        LimiterImpl.zkCacheOperations = zkCacheOperations;
    }

    public static void setSystemId(String systemId) {
        LimiterImpl.systemId = systemId;
    }

    public static String getBasePath() {
        return basePath;
    }
}
