package com.zlg.pressurer2.service;


import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.zlg.pressurer2.common.DeviceSecret;
import com.zlg.pressurer2.common.GlobalMqttClientList;
import com.zlg.pressurer2.common.GlobalWebClient;
import com.zlg.pressurer2.exception.BizException;
import com.zlg.pressurer2.helper.mqtt.SendData;
import com.zlg.pressurer2.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.*;

@Service
public class PressureService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static WebClient webClient = GlobalWebClient.getWebClient();
    private ScheduledFuture<?> scheduledFuture;
    @Value(value = "${testData.tenantTotal}")
    private int TEST_TENANT_TOTAL;
    @Value(value = "${testData.invertTotal}")
    private int INVERT_TOTAL;
    @Value(value = "${testData.canCommonTotal}")
    private int CAN_COMMON_TOTAL;
    private boolean pressureStop;

    @Resource
    private AsyncTaskService asyncTaskService;

    public void deviceOnline(Integer deviceNumber, String deviceType, Integer part, Integer rest, Integer startUserIndex, Integer startDeviceIndex) throws InterruptedException, ExecutionException {
        if (GlobalMqttClientList.mqttClientList != null && GlobalMqttClientList.mqttClientList.size() != 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "pressure.1004");
        }
        ArrayList<DeviceInfo> deviceInfoList = generatedDeviceInfoList(deviceNumber, deviceType, startUserIndex, startDeviceIndex);
        String deviceSecret = DeviceSecret.DEVICES_SECRET.get(deviceType);
        int i = 1;
        if (deviceInfoList.size() > 0) {
            pressureStop = false;
            ArrayList<PressureMqttClient> mqttClients = new ArrayList<>();
            ArrayList<Future<PressureMqttClient>> futureList = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            logger.debug("开始设备上线,设备数量: {}, 当前时间戳: {}", deviceInfoList.size(), startTime);
            for (DeviceInfo deviceInfo : deviceInfoList) {
                String thirdThingsId = deviceInfo.getThird_things_id();
                String tenantName = deviceInfo.getTenant_name();
                String parentsJson = "{\"devices\":[{\"devid\":\"" + thirdThingsId + "\",\"devtype\":\"" + deviceType + "\"}],\"password\":\"" + deviceSecret + "\",\"username\":\"" + deviceType + "\"}";

                Future<PressureMqttClient> mqttClientFuture = asyncTaskService.deviceOnline(deviceType, thirdThingsId, tenantName, parentsJson, webClient);
                futureList.add(mqttClientFuture);
                //每上线i个设备就休息一下
                if (i % part == 0) {
                    logger.debug("i = {},主线程休息", i);
                    Thread.sleep(rest);
                }
                i++;
                if (pressureStop) {
                    throw new RuntimeException("中途停止任务");
                }
            }
            logger.debug("futureList size: {}", futureList.size());
            long futureTime = System.currentTimeMillis();
            for (Future<PressureMqttClient> pressureMqttClientFuture : futureList) {
                if (null != pressureMqttClientFuture) {
                    PressureMqttClient pressureMqttClient = pressureMqttClientFuture.get();
                    mqttClients.add(pressureMqttClient);
                }
            }
            long endTime = System.currentTimeMillis();
            logger.debug("设备全部上线完成,mqttClient 数量: {}, 当前时间戳: {}", mqttClients.size(), endTime);
            logger.debug("总共耗时: {}", endTime - startTime);
            logger.debug("遍历阻塞队列futureList耗时: {}", endTime - futureTime);
            GlobalMqttClientList.mqttClientList = mqttClients;

            //临时修改,给can设备上报通道信息
//            for (PressureMqttClient mqttClient : mqttClients) {
//                String sendTopic = "/d2s/" + mqttClient.getTenantName() + "/" + mqttClient.getInfoModelName() + "/" + mqttClient.getThirdThingsId() + "/status";
//                String data = "\0CANInfo\0{\"CAN7\":{\"IsUpload\":0,\"Type\":0,\"Clk\":0,\"IsDownload\":0,\"Mode\":0,\"Enable\":1},\"CAN6\":{\"IsUpload\":0,\"Type\":0,\"Clk\":0,\"IsDownload\":0,\"Mode\":0,\"Enable\":1},\"CAN5\":{\"IsUpload\":0,\"Type\":0,\"Clk\":0,\"IsDownload\":0,\"Mode\":0,\"Enable\":1},\"CAN4\":{\"IsUpload\":0,\"Type\":0,\"Clk\":0,\"IsDownload\":0,\"Mode\":0,\"Enable\":1},\"CAN3\":{\"IsUpload\":0,\"Type\":0,\"Clk\":0,\"IsDownload\":0,\"Mode\":0,\"Enable\":1},\"CAN2\":{\"IsUpload\":0,\"Type\":0,\"Clk\":0,\"IsDownload\":0,\"Mode\":0,\"Enable\":1},\"CAN1\":{\"IsUpload\":0,\"Type\":0,\"Clk\":0,\"IsDownload\":0,\"Mode\":0,\"Enable\":1},\"CAN0\":{\"IsUpload\":0,\"Type\":0,\"Clk\":0,\"IsDownload\":0,\"Mode\":0,\"Enable\":1}}\0";
//                byte[] send = data.getBytes();
//                mqttClient.getMqttClient().publishWith().topic(sendTopic).qos(MqttQos.AT_LEAST_ONCE).payload(send).send();
//                logger.debug(sendTopic);
//            }
        } else {
            logger.error("deviceInfoList size is 0");
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "pressure.1001");
        }

    }

    public void pressureStart(Integer period, String topic, String data) throws ExecutionException, InterruptedException {

        ArrayList<PressureMqttClient> mqttClientList = GlobalMqttClientList.mqttClientList;
        if (null == mqttClientList || mqttClientList.size() == 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "pressure.1002");
        }
        byte[] send = Base64.getDecoder().decode(data);

        logger.debug("参与压测的设备数: {}", mqttClientList.size());
        SendData sendData = new SendData();
        sendData.setMqttClientList(mqttClientList);
        sendData.setAsyncTaskService(asyncTaskService);
        sendData.setPeriod(period);
        sendData.setSend(send);
        sendData.setTopic(topic);
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(4);
        scheduledFuture = executor.scheduleAtFixedRate(sendData, 0, period, TimeUnit.MILLISECONDS);
    }

    public void pressureStop() {
        pressureStop = true;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }

    /**
     * 构建压测设备信息
     *
     * @param deviceNumber     压测设备数
     * @param deviceType       设备类型
     * @param startUserIndex   压测用户下标，基于云端模拟数据实现的逻辑
     * @param startDeviceIndex 压测设备下标
     * @return 压测设备信息List
     */
    private ArrayList<DeviceInfo> generatedDeviceInfoList(Integer deviceNumber, String deviceType, Integer startUserIndex, Integer startDeviceIndex) {
        ArrayList<DeviceInfo> deviceInfos = new ArrayList<>(deviceNumber);
        if ("HE_BAT".equals(deviceType)) {
            for (int i = 1; i <= 100000; i++) {
                DeviceInfo deviceInfo = new DeviceInfo();
                deviceInfo.setTenant_name("linyouru");
                deviceInfo.setThird_things_id("he_bat_" + i);
                deviceInfos.add(deviceInfo);
                if (deviceInfos.size() == deviceNumber) {
                    break;
                }
            }
        } else {
            one:
            for (int i = startUserIndex; i <= TEST_TENANT_TOTAL; i++) {
                if ("invert".equals(deviceType)) {
                    for (int j = startDeviceIndex; j <= INVERT_TOTAL; j++) {
                        DeviceInfo deviceInfo = new DeviceInfo();
                        deviceInfo.setTenant_name("pressure" + i);
                        deviceInfo.setThird_things_id("device_invert_" + i + "_" + j);
                        deviceInfos.add(deviceInfo);
                        if (deviceInfos.size() == deviceNumber) {
                            break one;
                        }
                    }
                } else if ("can-common".equals(deviceType)) {
                    for (int j = startDeviceIndex; j <= CAN_COMMON_TOTAL; j++) {
                        DeviceInfo deviceInfo = new DeviceInfo();
                        deviceInfo.setTenant_name("pressure" + i);
                        deviceInfo.setThird_things_id("device_can_" + i + "_" + j);
                        deviceInfos.add(deviceInfo);
                        if (deviceInfos.size() == deviceNumber) {
                            break one;
                        }
                    }
                }
            }
        }
        return deviceInfos;
    }


}
