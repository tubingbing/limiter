package com.tbb.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tbb.domain.LimiterDomain;
import com.tbb.proxy.LimiterImpl;
import com.tbb.util.JsonUtil;
import com.tbb.util.ZKDeserializeUtil;
import com.tbb.zk.ZKCacheOperations;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: tubingbing
 * @Date: 2017/5/16
 * @Time: 10:00
 */
@Controller
@RequestMapping("/limiter")
public class LimiterController {
    private static final Logger logger = LoggerFactory.getLogger(LimiterController.class);

    @Resource
    private ZKCacheOperations zkOperation;

    /**
     * 查询限流配置信息
     *
     * @return
     */
    @RequestMapping("/query")
    public ModelAndView query(ModelAndView view, String systemId, String path) {
        view.addObject("path", path);
        view.addObject("systemId", systemId);
        view.addObject("systemInfoMap", getSystemInfoMap());
        view.setViewName("limiter/query");
        return view;
    }

    @RequestMapping("/queryList")
    public ModelAndView queryList(ModelAndView view, String systemId, String path) {
        view.setViewName("limiter/queryList");
        try {
            CuratorFramework client = zkOperation.getZkCacheClient().getClient();
            Map<String, LimiterDomain> map = new HashMap<String, LimiterDomain>();
            if (StringUtils.isEmpty(systemId)) {
                view.addObject("map", map);
                return view;
            }
            String basePath = transToZKPath(systemId, path);
            if (basePath.equals(LimiterImpl.getBasePath())) {
                createDefaultData(client, basePath);
            }
            query(client, basePath, map);
            view.addObject("map", map);
            logger.info("数据：" + JsonUtil.toStr(map));
        } catch (Exception e) {
            logger.error("exception", e);
        }
        view.addObject("path", path);
        view.addObject("systemId", systemId);
        view.addObject("systemInfoMap", getSystemInfoMap());
        return view;
    }

    private void query(CuratorFramework client, String path, Map<String, LimiterDomain> map) throws Exception {
        try {
            byte[] bytes = client.getData().forPath(path);
            String json = String.valueOf(ZKDeserializeUtil.getInstance().deserialize(bytes));
            LimiterDomain domain = JsonUtil.toObj(json, LimiterDomain.class);
            logger.info(path + " query:" + JsonUtil.toStr(domain));
            if (domain != null && domain.getTotalQps() >= 0) {
                map.put(domain.getPath(), domain);
            }
        } catch (Exception e) {
            logger.error("exception",e);
        }
        List<String> list = client.getChildren().forPath(path);
        for (String str : list) {
            query(client, path + "/" + str, map);
        }

    }

    /**
     * 创建默认数据
     *
     * @param client
     * @param basePath
     * @throws Exception
     */
    private void createDefaultData(CuratorFramework client, String basePath) throws Exception {
        String[] args = basePath.split("/");
        String str = "/";
        for (int i = 0; i < args.length; i++) {
            if (StringUtils.isEmpty(args[i])) {
                continue;
            }
            str += args[i];
            Stat stat = client.checkExists().forPath(str);
            if (stat == null) {
                LimiterDomain domain = new LimiterDomain();
                domain.setTotalQps(-1L);
                domain.setTime(-1);
                domain.setRequestQps(-1L);
                domain.setRequestType(new int[1]);
                domain.setPath(str);
                String json = JsonUtil.toStr(domain);
                client.create().forPath(str, ZKDeserializeUtil.getInstance().serialize(json));
            }
            str += "/";
        }
    }

    @RequestMapping("/add")
    public String add(Model model) {
        model.addAttribute("systemInfoMap", getSystemInfoMap());
        return "limiter/add";
    }

    @RequestMapping("/addSubmit")
    public void addSubmit(HttpServletResponse response, LimiterDomain limiterDomain) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String basePath = transToZKPath(limiterDomain.getSystemId(), limiterDomain.getPath());
            if (!limiterDomain.getPath().startsWith("/")) {
                limiterDomain.setPath("/" + limiterDomain.getPath());
            }
            if (limiterDomain.getPath().endsWith("/")) {
                limiterDomain.setPath(limiterDomain.getPath().substring(0, limiterDomain.getPath().length() - 1));
            }
            CuratorFramework client = zkOperation.getZkCacheClient().getClient();
            createDefaultData(client, basePath);
            LimiterDomain existDomain = null;
            try {
                String json = String.valueOf(ZKDeserializeUtil.getInstance().deserialize(client.getData().forPath(basePath)));
                existDomain = JsonUtil.toObj(json, LimiterDomain.class);
                logger.info(basePath + " add:" + json);
            } catch (Exception e) {
                logger.error("",e);
            }
            if (existDomain != null && existDomain.getTotalQps() > 0) {
                out.print("2");
            } else {
                client.setData().forPath(basePath, ZKDeserializeUtil.getInstance().serialize(JsonUtil.toStr(limiterDomain)));
                out.print("1");
            }
        } catch (Exception e) {
            logger.error("exception", e);
            out.print("-1");
        }
        if (out != null) {
            out.flush();
        }
    }

    @RequestMapping("/toBatchAdd")
    public String toBatchAdd(Model model) {
        model.addAttribute("systemInfoMap", getSystemInfoMap());
        return "limiter/batchAdd";
    }

    @RequestMapping("/batchAddSubmit")
    public void batchAddSubmit(HttpServletResponse response, LimiterDomain limiterDomain, String paths) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            CuratorFramework client = zkOperation.getZkCacheClient().getClient();
            String[] pathss = paths.split("\r\n");
            String existPath = "";
            for (String path : pathss) {
                limiterDomain.setPath(path);
                String basePath = transToZKPath(limiterDomain.getSystemId(), limiterDomain.getPath());
                if (!limiterDomain.getPath().startsWith("/")) {
                    limiterDomain.setPath("/" + limiterDomain.getPath());
                }
                if (limiterDomain.getPath().endsWith("/")) {
                    limiterDomain.setPath(limiterDomain.getPath().substring(0, limiterDomain.getPath().length() - 1));
                }
                createDefaultData(client, basePath);
                LimiterDomain existDomain = null;
                try {
                    String json = String.valueOf(ZKDeserializeUtil.getInstance().deserialize(client.getData().forPath(basePath)));
                    existDomain = JsonUtil.toObj(json, LimiterDomain.class);
                    logger.info(basePath + " add:" + json);
                } catch (Exception e) {
                    logger.error("",e);
                }
                if (existDomain != null && existDomain.getTotalQps() > 0) {
                    existPath += path + ",";
                } else {
                    client.setData().forPath(basePath, ZKDeserializeUtil.getInstance().serialize(JsonUtil.toStr(limiterDomain)));
                }
            }
            if (existPath != "") {
                out.print(existPath.substring(0, existPath.length() - 1));
            } else {
                out.print("1");
            }
        } catch (Exception e) {
            logger.error("exception", e);
            out.print("-1");
        }
        if (out != null) {
            out.flush();
        }
    }

    @RequestMapping("/update")
    public ModelAndView update(ModelAndView view, String systemId, String path) {
        try {
            String basePath = transToZKPath(systemId, path);
            CuratorFramework client = zkOperation.getZkCacheClient().getClient();
            String json = String.valueOf(ZKDeserializeUtil.getInstance().deserialize(client.getData().forPath(basePath)));
            LimiterDomain limiterDomain = JsonUtil.toObj(json, LimiterDomain.class);
            view.addObject("limiterDomain", limiterDomain);
            view.addObject("systemInfoMap", getSystemInfoMap());
        } catch (Exception e) {
            logger.error("exception", e);
        }
        view.setViewName("limiter/update");
        return view;
    }

    @RequestMapping("/updateSubmit")
    public void updateSubmit(HttpServletResponse response, LimiterDomain limiterDomain) {
        PrintWriter out = null;
        try {
            if (limiterDomain.getOpenBrush() == 0) {
                limiterDomain.setRequestType(null);
                limiterDomain.setTime(null);
                limiterDomain.setRequestQps(null);
            }
            out = response.getWriter();
            String basePath = transToZKPath(limiterDomain.getSystemId(), limiterDomain.getPath());
            CuratorFramework client = zkOperation.getZkCacheClient().getClient();
            client.setData().forPath(basePath, ZKDeserializeUtil.getInstance().serialize(JsonUtil.toStr(limiterDomain)));
            out.print("1");
        } catch (Exception e) {
            logger.error("exception", e);
            out.print("-1");
        }
        if (out != null) {
            out.flush();
        }
    }

    @RequestMapping("/toBatchUpdate")
    public ModelAndView toBatchUpdate(ModelAndView view, String systemId, String paths) {
        try {
            view.addObject("systemId", systemId);
            view.addObject("paths", paths);
        } catch (Exception e) {
            logger.error("exception", e);
        }
        view.setViewName("limiter/batchUpdate");
        return view;
    }

    @RequestMapping("/batchUpdate")
    public void batchUpdate(HttpServletResponse response, LimiterDomain limiterDomain, String paths) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            CuratorFramework client = zkOperation.getZkCacheClient().getClient();
            String[] pathss = paths.split(",");
            for (String path : pathss) {
                if (!StringUtils.isEmpty(path)) {
                    String basePath = transToZKPath(limiterDomain.getSystemId(), path);
                    limiterDomain.setPath(path);
                    client.setData().forPath(basePath, ZKDeserializeUtil.getInstance().serialize(JsonUtil.toStr(limiterDomain)));
                }
            }
            out.print("1");
        } catch (Exception e) {
            logger.error("exception", e);
            out.print("-1");
        }
        if (out != null) {
            out.flush();
        }
    }

    @RequestMapping("/delete")
    public void delete(HttpServletResponse response, String systemId, String path) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String basePath = transToZKPath(systemId, path);
            CuratorFramework client = zkOperation.getZkCacheClient().getClient();
            client.setData().forPath(basePath, null);
            //client.delete().forPath(basePath);
            out.print("1");
        } catch (Exception e) {
            logger.error("exception", e);
            out.print("-1");
        }
        if (out != null) {
            out.flush();
        }
    }

    @RequestMapping("/batchDelete")
    public void batchDelete(HttpServletResponse response, String systemId, String paths) {
        PrintWriter out = null;
        try {
            CuratorFramework client = zkOperation.getZkCacheClient().getClient();
            out = response.getWriter();
            String[] pathss = paths.split(",");
            for (String path : pathss) {
                if (!StringUtils.isEmpty(path)) {
                    String basePath = transToZKPath(systemId, path);
                    client.setData().forPath(basePath, null);
                }
            }
            //client.delete().forPath(basePath);
            out.print("1");
        } catch (Exception e) {
            logger.error("exception", e);
            out.print("-1");
        }
        if (out != null) {
            out.flush();
        }
    }

    /**
     * 路径转换
     *
     * @param value
     * @return
     */
    private String transToZKPath(String systemId, String value) {
        String path = LimiterImpl.getBasePath() + "/";
        if (!StringUtils.isEmpty(systemId)) {
            path += systemId;
        }
        if (!StringUtils.isEmpty(value)) {
            if (!value.startsWith("/")) {
                path += "/";
            }
            path += value;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private final static String limiterSystemId="limiterSystemId";

    @RequestMapping("/querySystemId")
    public ModelAndView querySystemId(ModelAndView view) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = RedisClient.getShardedJedis();
            if (shardedJedis != null) {
                Map<String, String> map = shardedJedis.hgetAll(limiterSystemId);
                Map<String, SystemIdDomain> systemMap = new HashMap<String, SystemIdDomain>();
                for (Map.Entry<String, String> m : map.entrySet()) {
                    systemMap.put(m.getKey(), JsonUtil.toObj(m.getValue(), SystemIdDomain.class));
                }
                view.addObject("systemMap", systemMap);
            }
        } catch (Exception e) {
            logger.error("exception", e);
        } finally {
            if (shardedJedis != null) {
                RedisClient.close(shardedJedis);
            }
        }
        view.setViewName("limiter/querySystemId");
        return view;
    }
    @RequestMapping("/querySystemIdList")
    public ModelAndView querySystemIdList(ModelAndView view) {
        querySystemId(view);
        view.setViewName("limiter/querySystemIdList");
        return view;
    }


    @RequestMapping("/updateSystemId")
    public ModelAndView updateSystemId(ModelAndView view, String systemId) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = RedisClient.getShardedJedis();
            if (shardedJedis != null) {
                if (!StringUtils.isEmpty(systemId)) {
                    String json = shardedJedis.hget(limiterSystemId,systemId);
                    SystemIdDomain domain = JsonUtil.toObj(json,new TypeReference<SystemIdDomain>() {
                    });
                    view.addObject("domain", domain);
                }
            }
        } catch (Exception e) {
            logger.error("exception", e);
        } finally {
            if (shardedJedis != null) {
                RedisClient.close(shardedJedis);
            }
        }
        view.setViewName("limiter/updateSystemId");
        return view;
    }

    @RequestMapping("/updateSystemIdSubmit")
    public void updateSystemIdSubmit(HttpServletResponse response, SystemIdDomain domain) {
        PrintWriter out = null;
        ShardedJedis shardedJedis = null;
        try {
            out = response.getWriter();
            shardedJedis = RedisClient.getShardedJedis();
            if (shardedJedis != null) {
                shardedJedis.hset(limiterSystemId,domain.getSystemId(),JsonUtil.toStr(domain));
                out.print("1");
            }
        } catch (Exception e) {
            logger.error("exception", e);
            out.print("-1");
        } finally {
            if (shardedJedis != null) {
                RedisClient.close(shardedJedis);
            }
        }
        if (out != null) {
            out.flush();
        }
    }

    private Map<String, SystemIdDomain> getSystemInfoMap() {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = RedisClient.getShardedJedis();
            if (shardedJedis != null) {
                Map<String, String> map = shardedJedis.hgetAll("limiterSystemId");
                Map<String, SystemIdDomain> systemMap = new HashMap<String, SystemIdDomain>();
                for (Map.Entry<String, String> m : map.entrySet()) {
                    systemMap.put(m.getKey(), JsonUtil.toObj(m.getValue(), SystemIdDomain.class));
                }
                return systemMap;
            }
        } catch (Exception e) {
            logger.error("",e);
        } finally {
            if (shardedJedis != null) {
                RedisClient.close(shardedJedis);
            }
        }
        return null;
    }
}
