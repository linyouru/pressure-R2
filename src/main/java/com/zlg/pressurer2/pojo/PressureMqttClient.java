package com.zlg.pressurer2.pojo;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;

public class PressureMqttClient {

    private Mqtt3AsyncClient mqttClient;
    private String infoModelName;
    private String tenantName;
    private String thirdThingsId;

    public Mqtt3AsyncClient getMqttClient() {
        return mqttClient;
    }

    public void setMqttClient(Mqtt3AsyncClient mqttClient) {
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
