package com.tbb.zk;

import com.tbb.exception.ZKCacheException;
import com.tbb.util.ZKDeserializeUtil;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.common.PathUtils;

public class ZKCacheOperations<V> {
    private ZKCacheClient zooKeeperCacheClient;

    public ZKCacheOperations() {
    }

    public void registerWatcher(String path, TreeCacheListener treeCacheListener) throws KeeperException, InterruptedException {
        this.zooKeeperCacheClient.registerTreeCache(path, treeCacheListener);
    }

    public V get(String path) throws KeeperException, InterruptedException {
        PathUtils.validatePath(path);
        ZKCache cache = ZKCache.getInstance();
        Object value;
        if(cache.containsPath(path)) {
            value = cache.getData(path);
            return (V)value;
        } else {
            value = null;
            if(this.zooKeeperCacheClient.getClient() != null && this.zooKeeperCacheClient.getClient().getZookeeperClient() != null && this.zooKeeperCacheClient.getClient().getZookeeperClient().isConnected()) {
                try {
                    this.register(path);
                    byte[] e = this.zooKeeperCacheClient.getClient().getData().forPath(path);
                    if(e != null) {
                        value = ZKDeserializeUtil.getInstance().deserialize(e);
                    }
                } catch (Exception var8) {
                    throw new ZKCacheException(var8);
                } finally {
                    cache.cacheData(path, value);
                }

                return (V)value;
            } else {
                throw new ZKCacheException("zookeeper is not initialized");
            }
        }
    }

    private void register(String path) throws KeeperException, InterruptedException {
        this.zooKeeperCacheClient.registerTreeCache(path, this.zooKeeperCacheClient.getTreeCacheListener());
    }

    public ZKCacheClient getZKCacheClient() {
        return this.zooKeeperCacheClient;
    }

    public void setZKCacheClient(ZKCacheClient zooKeeperCacheClient) {
        this.zooKeeperCacheClient = zooKeeperCacheClient;
    }
}
