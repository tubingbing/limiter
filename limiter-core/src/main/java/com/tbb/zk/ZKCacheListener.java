package com.tbb.zk;

import com.tbb.util.ZKDeserializeUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

public class ZKCacheListener implements TreeCacheListener {
    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
        switch (event.getType()) {
            case NODE_ADDED: {
                Object value = ZKDeserializeUtil.getInstance().deserialize(event.getData().getData());
                ZKCache.getInstance().cacheData(event.getData().getPath(), value);
                break;
            }
            case NODE_UPDATED: {
                Object value = ZKDeserializeUtil.getInstance().deserialize(event.getData().getData());
                ZKCache.getInstance().cacheData(event.getData().getPath(), value);
                break;
            }
            case NODE_REMOVED: {
                ZKCache.getInstance().remove(event.getData().getPath());
                break;
            }
            default:
                //其他情况
        }
    }
}
