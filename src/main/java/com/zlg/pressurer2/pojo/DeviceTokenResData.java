package com.zlg.pressurer2.pojo;

public class DeviceTokenResData {
    private String clientip;
    private String owner;
    private String token;
    private String uuid;
    private DeviceTokenResDataMqtt mqtt;

    public String getClientip() {
        return clientip;
    }

    public void setClientip(String clientip) {
        this.clientip = clientip;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public DeviceTokenResDataMqtt getMqtt() {
        return mqtt;
    }

    public void setMqtt(DeviceTokenResDataMqtt mqtt) {
        this.mqtt = mqtt;
    }

    @Override
    public String toString() {
        return "Data{" +
                "clientip='" + clientip + '\'' +
                ", owner='" + owner + '\'' +
                ", token='" + token + '\'' +
                ", uuid='" + uuid + '\'' +
                ", mqtt=" + mqtt +
                '}';
    }


}
