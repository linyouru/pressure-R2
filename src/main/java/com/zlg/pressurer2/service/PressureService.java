package com.zlg.pressurer2.service;


import com.zlg.pressurer2.common.DeviceSecret;
import com.zlg.pressurer2.common.GlobalMqttClientList;
import com.zlg.pressurer2.common.GlobalDeviceList;
import com.zlg.pressurer2.common.GlobalWebClient;
import com.zlg.pressurer2.exception.BizException;
import com.zlg.pressurer2.helper.mqtt.SendData;
import com.zlg.pressurer2.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
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
        //目前云平台只准备了最多50万设备
        if (deviceNumber > 500000) {
            throw new BizException(HttpStatus.BAD_REQUEST, "pressure.1003");
        }
        if (GlobalMqttClientList.mqttClientList != null && GlobalMqttClientList.mqttClientList.size() != 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "pressure.1004");
        }
//        ArrayList<DeviceInfo> allDeviceInfoList = globalDeviceList.getAllDeviceInfoList();
        ArrayList<DeviceInfo> deviceInfoList = generatedDeviceInfoList(deviceNumber, deviceType);
        String deviceSecret = DeviceSecret.DEVICES_SECRET.get(deviceType);
        int i = 1;
        if (deviceInfoList.size() > 0) {
            ArrayList<PressureMqttClient> mqttClients = new ArrayList<>();
            ArrayList<Future<PressureMqttClient>> futureList = new ArrayList<>();
//            pressureDeviceInfoList = allDeviceInfoList.subList(0, deviceNumber);
            long startTime = System.currentTimeMillis();
            logger.info("开始设备上线,设备数量{}, 当前时间戳{}", deviceInfoList.size(), startTime);
            for (DeviceInfo deviceInfo : deviceInfoList) {
                String thirdThingsId = deviceInfo.getThird_things_id();
                String tenantName = deviceInfo.getTenant_name();
                String parentsJson = "{\"devices\":[{\"devid\":\"" + thirdThingsId + "\",\"devtype\":\"" + deviceType + "\"}],\"password\":\"" + deviceSecret + "\",\"username\":\"" + deviceType + "\"}";

                Future<PressureMqttClient> mqttClientFuture = asyncTaskService.deviceOnline(deviceType, thirdThingsId, tenantName, parentsJson, webClient);
                futureList.add(mqttClientFuture);
                //每上线i个设备就休息一下
                if (i % part == 0) {
                    logger.info("i = {},主线程休息", i);
                    Thread.sleep(rest);
                }
                i++;
            }
            logger.info("futureList size {}", futureList.size());
            for (Future<PressureMqttClient> pressureMqttClientFuture : futureList) {
                PressureMqttClient pressureMqttClient = pressureMqttClientFuture.get();
                mqttClients.add(pressureMqttClient);
            }
            long endTime = System.currentTimeMillis();
            logger.info("设备全部上线完成,mqttClient 数量{}, 当前时间戳{}", mqttClients.size(), endTime);
            logger.info("总共耗时{}", endTime - startTime);
            GlobalMqttClientList.mqttClientList = mqttClients;
        } else {
            logger.error("allDeviceInfoList size is 0");
            throw new BizException(HttpStatus.BAD_REQUEST, "pressure.1001");
        }

    }

    public void pressureStart(Integer period, String topic, String data) throws ExecutionException, InterruptedException {

        ArrayList<PressureMqttClient> mqttClientList = GlobalMqttClientList.mqttClientList;
        if (null == mqttClientList || mqttClientList.size() == 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "pressure.1002");
        }
        byte[] send = Base64.getDecoder().decode(data);

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

    private ArrayList<DeviceInfo> generatedDeviceInfoList(Integer deviceNumber, String deviceType) {
        ArrayList<DeviceInfo> deviceInfos = new ArrayList<>(deviceNumber);
        one:
        for (int i = 1000; i >= 1; i--) {
            if ("invert".equals(deviceType)) {
                for (int j = 1; j <= 500; j++) {
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.setTenant_name("pressure" + i);
                    deviceInfo.setThird_things_id("device_invert_" + i + "_" + j);
                    deviceInfos.add(deviceInfo);
                    if (deviceInfos.size() == deviceNumber) {
                        break one;
                    }
                }
            } else if ("can-common".equals(deviceType)) {
                for (int k = 1; k <= 2; k++) {
                    DeviceInfo deviceInfo = new DeviceInfo();
                    deviceInfo.setTenant_name("pressure" + i);
                    deviceInfo.setThird_things_id("device_can_" + i + "_" + k);
                    deviceInfos.add(deviceInfo);
                    if (deviceInfos.size() == deviceNumber) {
                        break one;
                    }
                }
            }
        }
        return deviceInfos;
    }


}
