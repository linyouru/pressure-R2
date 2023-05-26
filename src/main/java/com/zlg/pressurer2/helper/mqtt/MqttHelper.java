package com.zlg.pressurer2.helper.mqtt;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Component
public class MqttHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //绑定本地ip

    @Value(value = "${localAddress}")
    public String localAddress;
    public ArrayList<InetSocketAddress> inetSocketAddressList = new ArrayList<>();
    public int flag = 0;
    public int localAddressTotal = 0;

    @PostConstruct
    private void init() {
        String[] localAddressArray = localAddress.split(",");
        localAddressTotal = localAddressArray.length;
        for (String localAddress : localAddressArray) {
            inetSocketAddressList.add(new InetSocketAddress(localAddress, 0));
        }
    }

    public Mqtt3AsyncClient getMqttClientByHiveMQ(String host, int port, String clientId, String userName, String password, String deviceType, String thirdThingsId, String tenantName) {
        //轮询分配IP
        flag = flag < localAddressTotal - 1 ? flag + 1 : 0;
        InetSocketAddress inetSocketAddress = inetSocketAddressList.get(flag);

        Mqtt3AsyncClient client = Mqtt3Client.builder()
                .identifier(clientId)
                .serverHost(host)
                .serverPort(port)
                .transportConfig()
                .mqttConnectTimeout(30000, TimeUnit.MILLISECONDS)
                .socketConnectTimeout(30000, TimeUnit.MILLISECONDS)
                .localAddress(inetSocketAddress)
                .applyTransportConfig()
                .buildAsync();


        client.connectWith()
                .simpleAuth()
                .username(userName)
                .password(password.getBytes())
                .applySimpleAuth()
                .send()
                .whenComplete((mqtt3ConnAck, throwable) -> {
                    //本地调试注释掉，因为收不到mqtt响应一定会超时
                    if (throwable != null) {
                        logger.error("连接mqtt失败:{},error:{}", clientId, throwable.getMessage());
//                        throwable.printStackTrace();
                    } else {
                        client.publishWith()
                                .topic("/d2s/" + tenantName + "/" + deviceType + "/" + thirdThingsId + "/online")
                                .payload("设备上线" .getBytes())
                                .send();
                    }
                });
        return client;

    }

}
