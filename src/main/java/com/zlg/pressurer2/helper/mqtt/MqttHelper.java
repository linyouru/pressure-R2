package com.zlg.pressurer2.helper.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Component;

@Component
public class MqttHelper {

    public static MqttClient getmqttClient(String serverURI, String clientId, String userName, String password) throws MqttException {

        MqttClientPersistence persistence = new MemoryPersistence();
        MqttClient client = new MqttClient(serverURI, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setUserName(userName);
        connOpts.setPassword(password.toCharArray());
        connOpts.setConnectionTimeout(30);
        connOpts.setCleanSession(true);

        client.connect(connOpts);
        return client;
    }

}
