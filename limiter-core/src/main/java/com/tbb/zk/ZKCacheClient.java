package com.tbb.zk;

import com.tbb.exception.ZKCacheException;
import com.tbb.exception.ZKCacheStateException;
import com.tbb.util.StringUtil;
import com.tbb.util.ZKInitUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZKCacheClient {
    // zookeeper ip
    private String ip;
    // zookeeper 端口
    private String port;

    // ip和端口url
    private String url;

    private String index;

    // 需要监听的base path
    private String basePath;

    // 需要监听的path
    private String paths;

    private Boolean sync;

    private CuratorFramework client;

    private TreeCacheListener treeCacheListener;


    public void init() {
        if (sync != null && !sync) {
            ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
            singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    initZookeeper();
                }
            });
            return;
        }
        initZookeeper();
    }

    private void initZookeeper() {
        if (basePath == null) {
            basePath = "/o2o/zk/cache";
        }

        while (StringUtil.isEmpty(this.url)) {
            if (StringUtil.isNotEmpty(this.ip) && StringUtil.isNotEmpty(this.port)) {
                this.url = ZKInitUtil.getUrlByIp(ip, port);
            } else {
                this.url = ZKInitUtil.getUrlByIndex(index);
            }
            if (StringUtil.isNotEmpty(this.url)) {
                ZKInitUtil.writeUrlToFile(url);
            } else {
                url = ZKInitUtil.readUrlFromFile();
            }
        }
        String nameSpace = this.basePath;
        if(this.basePath.startsWith("/")) {
            nameSpace = this.basePath.substring(1);
        }
        client = CuratorFrameworkFactory.builder().namespace(nameSpace).connectString(url).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        client.start();
        dealPaths();
    }


    /**
     * 监听path
     */
    private void dealPaths() {
        if (treeCacheListener == null) {
            treeCacheListener = new ZKCacheListener();
        }
        if (paths == null) {
            return;
        }
        String[] pathArray = paths.split(",");
        for (String p : pathArray) {
            registerTreeCache(basePath + p, treeCacheListener);
        }
    }

    public void registerTreeCache(String path, TreeCacheListener treeCacheListener) {
        if (client == null)
            throw new ZKCacheException("zookeeper is not initialized");
        final TreeCache cache = new TreeCache(client, path);
        try {
            cache.start();
        } catch (Exception e) {
            throw new ZKCacheStateException("TreeCache start error.");
        }
        cache.getListenable().addListener(treeCacheListener);
    }


    public TreeCacheListener getTreeCacheListener() {
        return treeCacheListener;
    }

    public void setTreeCacheListener(TreeCacheListener treeCacheListener) {
        this.treeCacheListener = treeCacheListener;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
