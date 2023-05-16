package com.zlg.pressurer2.service;

import com.zlg.pressurer2.helper.mqtt.MqttHelper;
import com.zlg.pressurer2.pojo.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.concurrent.Future;

@Component
public class AsyncTaskService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Async
//    public void asyncTest(int i){
//        logger.info("线程ID：" + Thread.currentThread().getId() +" 线程名字：" +Thread.currentThread().getName()+" 获取租户pressure{}的设备列表", i);
//    }

    @Async
    public Future<DeviceInfoList> getDeviceInfoList(String devType, LoginRequest loginRequest, int i,
                                                    HashMap<String, Integer> publicInfoModel, WebClient webClient) {
        loginRequest.setUsername("pressure" + i);
        LoginRes loginRes = apiTenantLogin(loginRequest, webClient);

        String authorization = "Bearer " + loginRes.getToken();

        Integer tenantId = loginRes.getTenant_id();
        Integer infoModelId = publicInfoModel.get(devType);
        DeviceInfoList deviceInfoList = apiGetDeviceInfoList(authorization, tenantId, infoModelId, webClient);
        logger.info("线程ID：" + Thread.currentThread().getId() + " 线程名字：" + Thread.currentThread().getName() + " 获取租户pressure{}的设备列表", i);
        return new AsyncResult<>(deviceInfoList);
    }

    /**
     * 获取租户设备列表
     *
     * @param authorization token
     * @param tenantId      租户id
     * @param infoModelId   设备类型id
     * @return
     */
    private DeviceInfoList apiGetDeviceInfoList(String authorization, Integer tenantId, Integer infoModelId, WebClient webClient) {
        Mono<DeviceInfoList> deviceInfoListMono = webClient.get()
                .uri("/things/tenants/" + tenantId + "/things?current_page=1&info_model_ids=" + infoModelId + "&page_size=10000")
                .header("Authorization", authorization)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DeviceInfoList.class);
        return deviceInfoListMono.block();
    }

    /**
     * 租户登录
     *
     * @param loginRequest 登录请求参数
     * @return
     */
    private LoginRes apiTenantLogin(LoginRequest loginRequest, WebClient webClient) {
        Mono<LoginRequest> req = Mono.just(loginRequest);
        Mono<LoginRes> loginResMono = webClient.post()
                .uri("/control/sessions/tenant-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req, LoginRequest.class)
                .retrieve()
                .bodyToMono(LoginRes.class);
        return loginResMono.block();
    }

    @Async
    public Future<PressureMqttClient> deviceOnline(String deviceType, String thirdThingsId, String tenantName, String parentsJson, WebClient webClient) {
        DeviceTokenRes deviceTokenRes = getDeviceTokenRes(parentsJson, webClient);
        assert deviceTokenRes != null;
        DeviceTokenResDataMqtt mqtt = deviceTokenRes.getData().getMqtt();

        String serverUri = "tcp://" + mqtt.getHost() + ":" + mqtt.getPort();
        String clientId = deviceType + ":" + thirdThingsId;
        String deviceToken = deviceTokenRes.getData().getToken();
        try {
            MqttClient mqttClient = MqttHelper.getmqttClient(serverUri.trim(), clientId, clientId, deviceToken);
            MqttMessage mqttMessage = new MqttMessage("设备上线".getBytes());
//            logger.info("[线程ID： {}] 设备{} 上线时刻: {}", Thread.currentThread().getId(), clientId, System.currentTimeMillis());
            //开发调试时Qos设为0，因为在虚拟机里收不到服务端响应
            mqttMessage.setQos(0);
            mqttClient.publish("/d2s/" + tenantName + "/" + deviceType + "/" + thirdThingsId + "/online", mqttMessage);
            PressureMqttClient pressureMqttClient = new PressureMqttClient();
            pressureMqttClient.setMqttClient(mqttClient);
            pressureMqttClient.setInfoModelName(deviceType);
            pressureMqttClient.setTenantName(tenantName);
            pressureMqttClient.setThirdThingsId(thirdThingsId);
            return new AsyncResult<>(pressureMqttClient);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取设备登录token
     *
     * @param parentsJson
     * @param webClient
     * @return
     */
    private DeviceTokenRes getDeviceTokenRes(String parentsJson, WebClient webClient) {
        Mono<DeviceTokenRes> deviceTokenResMono = webClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(parentsJson)
                .retrieve()
                .bodyToMono(DeviceTokenRes.class)
                .doOnError(WebClientResponseException.class, err -> {
                    logger.error("获取设备登录token发生错误：" + err.getRawStatusCode() + " " + err.getResponseBodyAsString());
                    throw new RuntimeException(err.getResponseBodyAsString());
                });
        return deviceTokenResMono.block();
    }

    @Async
    public void deviceSendData(PressureMqttClient pressureMqttClient, String send, String type) {
        MqttClient mqttClient = pressureMqttClient.getMqttClient();
        String topic = new StringBuilder("/d2s/")
                .append(pressureMqttClient.getTenantName())
                .append("/").append(pressureMqttClient.getInfoModelName())
                .append("/").append(pressureMqttClient.getThirdThingsId())
                .append("/").append(type).toString();
        MqttMessage mqttMessage = new MqttMessage(send.getBytes());
        mqttMessage.setQos(0);
        try {
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            logger.error("mqttClient上报数据出错,topic:{}", topic, e);
            throw new RuntimeException(e);
        }
        logger.info("[线程ID： {}] time: {} topic:{}", Thread.currentThread().getId(), System.currentTimeMillis(), topic);
    }
}
