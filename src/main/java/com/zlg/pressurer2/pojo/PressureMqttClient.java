package com.zlg.pressurer2.pojo;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class PressureMqttClient {

    private MqttClient mqttClient;
    private String infoModelName;
    private String tenantName;
    private String thirdThingsId;

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public void setMqttClient(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public String getInfoModelName() {
        return infoModelName;
    }

    public void setInfoModelName(String infoModelName) {
        this.infoModelName = infoModelName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getThirdThingsId() {
        return thirdThingsId;
    }

    public void setThirdThingsId(String thirdThingsId) {
        this.thirdThingsId = thirdThingsId;
    }

    @Override
    public String toString() {
        return "PressureMqttClient{" +
                "mqttClient=" + mqttClient +
                ", infoModelName='" + infoModelName + '\'' +
                ", tenantName='" + tenantName + '\'' +
                ", thirdThingsId='" + thirdThingsId + '\'' +
                '}';
    }
}
