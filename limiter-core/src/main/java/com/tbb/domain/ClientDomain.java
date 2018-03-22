package com.tbb.domain;

/**
 * @author: tubingbing
 * @Date: 2017/5/15
 * @Time: 18:28
 */
public class ClientDomain {
    
    private String pin ;//用户pin
    private String deviceId; //设备号
    private String ip;  //ip
    private String platCode;//平台 h5 android ios

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPlatCode() {
        return platCode;
    }

    public void setPlatCode(String platCode) {
        this.platCode = platCode;
    }
}
