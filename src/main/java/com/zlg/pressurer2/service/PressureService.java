package com.zlg.pressurer2.service;


import com.zlg.pressurer2.common.DeviceSecret;
import com.zlg.pressurer2.common.GlobaMqttClientList;
import com.zlg.pressurer2.common.GlobalDeviceList;
import com.zlg.pressurer2.common.GlobalWebClient;
import com.zlg.pressurer2.exception.BizException;
import com.zlg.pressurer2.helper.mqtt.MqttHelper;
import com.zlg.pressurer2.helper.mqtt.SendData;
import com.zlg.pressurer2.pojo.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.*;

@Service
public class PressureService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static WebClient webClient = GlobalWebClient.getWebClient();
    private List<DeviceInfo> pressureDeviceInfoList;
    private ScheduledFuture<?> scheduledFuture;

    @Resource
    private GlobalDeviceList globalDeviceList;
    @Resource
    private AsyncTaskService asyncTaskService;

    public void deviceOnline(Integer deviceNumber, String deviceType, Integer part, Integer rest) throws InterruptedException, ExecutionException {

        ArrayList<DeviceInfo> allDeviceInfoList = globalDeviceList.getAllDeviceInfoList();
        String deviceSecret = DeviceSecret.DEVICES_SECRET.get(deviceType);
        int i = 1;
        if (null != allDeviceInfoList && allDeviceInfoList.size() > 0) {

            ArrayList<PressureMqttClient> mqttClients = new ArrayList<>();
            ArrayList<Future<PressureMqttClient>> futureList = new ArrayList<>();
            pressureDeviceInfoList = allDeviceInfoList.subList(0, deviceNumber);
            for (DeviceInfo deviceInfo : pressureDeviceInfoList) {
                String thirdThingsId = deviceInfo.getThird_things_id();
                String tenantName = deviceInfo.getTenant_name();
                String parentsJson = "{\"devices\":[{\"devid\":\"" + thirdThingsId + "\",\"devtype\":\"" + deviceType + "\"}],\"password\":\"" + deviceSecret + "\",\"username\":\"" + deviceType + "\"}";

                Future<PressureMqttClient> mqttClientFuture = asyncTaskService.deviceOnline(deviceType, thirdThingsId, tenantName, parentsJson, webClient);
                futureList.add(mqttClientFuture);
                //每上线10个设备就休息一下
                if (i % part == 0) {
                    logger.info("i = {},主线程休息", i);
                    Thread.sleep(rest);
                }
                i++;
            }
            for (Future<PressureMqttClient> PressureMqttClientFuture : futureList) {
                PressureMqttClient pressureMqttClient = PressureMqttClientFuture.get();
                mqttClients.add(pressureMqttClient);
            }
            logger.info("mqttClient 数量{}, 当前时间戳{}", mqttClients.size(), System.currentTimeMillis());
            GlobaMqttClientList.mqttClientList = mqttClients;
        } else {
            logger.error("allDeviceInfoList size is 0");
            throw new BizException(HttpStatus.BAD_REQUEST, "pressure.1001");
        }

    }

    public void pressureStart(Integer period, String topic, String data) throws ExecutionException, InterruptedException {

        ArrayList<PressureMqttClient> mqttClientList = GlobaMqttClientList.mqttClientList;
        if (null == mqttClientList && mqttClientList.size() == 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "pressure.1002");
        }
        String send = data;
        if (topic.equals("data")) {
            byte[] decode = Base64.getDecoder().decode(data);
            send = new String(decode, StandardCharsets.UTF_8);
        }

        logger.info("参与压测的设备数:{}", mqttClientList.size());
        SendData sendData = new SendData();
        sendData.setMqttClientList(mqttClientList);
        sendData.setAsyncTaskService(asyncTaskService);
        sendData.setPeriod(period);
        sendData.setSend(send);
        sendData.setTopic(topic);
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(4);
        scheduledFuture = executor.scheduleAtFixedRate(sendData, 0, period, TimeUnit.SECONDS);
    }

    public void pressureStop() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }


}
