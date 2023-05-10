package com.zlg.pressurer2.pojo;

public class DeviceTokenResDataMqtt {
    private String host;
    private Integer port;
    private Integer sslport;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getSslport() {
        return sslport;
    }

    public void setSslport(Integer sslport) {
        this.sslport = sslport;
    }

    @Override
    public String toString() {
        return "Mqtt{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", sslport=" + sslport +
                '}';
    }
}
