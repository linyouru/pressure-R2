package com.zlg.pressurer2.pojo;

public class DeviceTokenRes {

    private Boolean result;
    private String message;
    private DeviceTokenResData data;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DeviceTokenResData getData() {
        return data;
    }

    public void setData(DeviceTokenResData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DevcieTokenRes{" +
                "result=" + result +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    
}

