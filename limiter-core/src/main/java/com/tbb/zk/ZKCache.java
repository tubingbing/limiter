package com.tbb.zk;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 缓存
 */
public class ZKCache{
    private static ZKCache instance = new ZKCache();

    // value默认值
    private static final Object PRESENT = new Object();

    private ConcurrentMap<String, Object> dataMap = new ConcurrentHashMap<String, Object>();

    private static final boolean SIGN ;
    static {
        SIGN = true;
    }

    // hide constructor
    private ZKCache() {
        if (SIGN) {
            throw new RuntimeException("we shouldn't create singleton twice");
        }
    }

    public static ZKCache getInstance() {
        return instance;
    }

    public void cacheData(String path, Object data) {
        // ConcurrentHashMap不支持null value
        if (data == null) {
            data = PRESENT;
        }
        dataMap.put(path, data);
    }

    public void cacheIfDataAbsent(String path, Object data) {
        // ConcurrentHashMap不支持null value
        if (data == null) {
            data = PRESENT;
        }
        dataMap.putIfAbsent(path, data);
    }

    public Object getData(String path) {
        Object data = dataMap.get(path);
        // 还原数据
        if (data == PRESENT) {
            data = null;
        }
        return data;
    }

    public boolean containsPath(String path) {
        boolean hasKey = dataMap.containsKey(path);
        return hasKey;
    }

    public void remove(String path) {
        dataMap.remove(path);
    }

    // invoke this method if we don't need instance any more
    public static void destroy() {
        instance = null;
    }
}
