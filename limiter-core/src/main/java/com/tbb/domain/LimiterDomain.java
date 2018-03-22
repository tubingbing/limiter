package com.tbb.domain;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author: tubingbing
 * @Date: 2017/5/15
 * @Time: 13:42
 */
public class LimiterDomain implements Serializable {
    //路径接口
    private String path;
    //系统id
    private String systemId;
    //接口每秒允许通过的请求次数
    private Long totalQps;
    //同一ip，用户每秒允许通过的次数
    private Long requestQps;
    //请求类型
    private int[] requestType;
    //限制时间—同一ip，用户超过每秒允许通过的次数  默认分钟
    private Integer time;
    //是否开启限流  1:开启 0：否 默认0
    private Integer openLimiter;
    //是否开启防刷 1：开启 0：否 默认0
    private Integer openBrush;

    public Long getTotalQps() {
        return totalQps;
    }

    public void setTotalQps(Long totalQps) {
        this.totalQps = totalQps;
    }

    public Long getRequestQps() {
        return requestQps;
    }

    public void setRequestQps(Long requestQps) {
        this.requestQps = requestQps;
    }

    public int[] getRequestType() {
        return requestType;
    }

    public void setRequestType(int[] requestType) {
        this.requestType = requestType;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getOpenLimiter() {
        return openLimiter;
    }

    public void setOpenLimiter(Integer openLimiter) {
        this.openLimiter = openLimiter;
    }

    public Integer getOpenBrush() {
        return openBrush;
    }

    public void setOpenBrush(Integer openBrush) {
        this.openBrush = openBrush;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "LimiterDomain{" +
                "path='" + path + '\'' +
                ", systemId='" + systemId + '\'' +
                ", totalQps=" + totalQps +
                ", requestQps=" + requestQps +
                ", requestType=" + Arrays.toString(requestType) +
                ", time=" + time +
                ", openLimiter=" + openLimiter +
                ", openBrush=" + openBrush +
                '}';
    }
}
