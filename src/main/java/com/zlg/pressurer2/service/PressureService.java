package com.zlg.pressurer2.service;


import com.zlg.pressurer2.common.DeviceSecret;
import com.zlg.pressurer2.common.GlobalDeviceList;
import com.zlg.pressurer2.common.GlobalWebClient;
import com.zlg.pressurer2.helper.mqtt.MqttHelper;
import com.zlg.pressurer2.pojo.DeviceInfo;
import com.zlg.pressurer2.pojo.DeviceInfoList;
import com.zlg.pressurer2.pojo.DeviceTokenRes;
import com.zlg.pressurer2.pojo.DeviceTokenResDataMqtt;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class PressureService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static WebClient webClient = GlobalWebClient.getWebClient();

    @Resource
    private GlobalDeviceList globalDeviceList;
    @Resource
    private AsyncTaskService asyncTaskService;

    public void pressureStart(Integer deviceNumber, String deviceType, Integer period, String topic, String data, Integer onlineTime) throws ExecutionException, InterruptedException {

        ArrayList<DeviceInfo> allDeviceInfoList = globalDeviceList.getAllDeviceInfoList();
        String deviceSecret = DeviceSecret.DEVICES_SECRET.get(deviceType);

        if (allDeviceInfoList.size() > 0) {

            ArrayList<MqttClient> mqttClients = new ArrayList<>();
            ArrayList<Future<MqttClient>> futureList = new ArrayList<>();
            List<DeviceInfo> pressureDeviceInfoList = allDeviceInfoList.subList(0, deviceNumber);
            for (DeviceInfo deviceInfo : pressureDeviceInfoList) {
                String thirdThingsId = deviceInfo.getThird_things_id();
                String tenantName = deviceInfo.getTenant_name();
                String parentsJson = "{\"devices\":[{\"devid\":\"" + thirdThingsId + "\",\"devtype\":\"" + deviceType + "\"}],\"password\":\"" + deviceSecret + "\",\"username\":\"" + deviceType + "\"}";

                Future<MqttClient> mqttClientFuture = asyncTaskService.deviceOnline(deviceType, thirdThingsId, tenantName, parentsJson, webClient);
                futureList.add(mqttClientFuture);
            }
            for (Future<MqttClient> mqttClientFuture : futureList) {
                MqttClient mqttClient = mqttClientFuture.get();
                mqttClients.add(mqttClient);
            }


        } else {
            logger.error("allDeviceInfoList length is 0");
        }

    }



}
