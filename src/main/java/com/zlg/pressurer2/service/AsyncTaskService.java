package com.zlg.pressurer2.service;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.zlg.pressurer2.helper.mqtt.MqttHelper;
import com.zlg.pressurer2.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.Future;

@Component
public class AsyncTaskService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MqttHelper mqttHelper;

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

    @Async
    public Future<PressureMqttClient> deviceOnline(String deviceType, String thirdThingsId, String tenantName, String parentsJson, WebClient webClient) {
//        logger.info("请求设备token开始,thirdThingsId: {} time: {}",thirdThingsId,System.currentTimeMillis());
        DeviceTokenRes deviceTokenRes = getDeviceTokenRes(parentsJson, webClient);
//        logger.info("请求设备token响应,thirdThingsId: {} time: {}",thirdThingsId,System.currentTimeMillis());
        assert deviceTokenRes != null;
        DeviceTokenResDataMqtt mqtt = deviceTokenRes.getData().getMqtt();

        String clientId = deviceType + ":" + thirdThingsId;
        String deviceToken = deviceTokenRes.getData().getToken();
        Mqtt3AsyncClient mqttClient = mqttHelper.getMqttClientByHiveMQ(mqtt.getHost(), mqtt.getPort(), clientId, clientId, deviceToken, deviceType, thirdThingsId, tenantName);
        PressureMqttClient pressureMqttClient = new PressureMqttClient();
        pressureMqttClient.setMqttClient(mqttClient);
        pressureMqttClient.setInfoModelName(deviceType);
        pressureMqttClient.setTenantName(tenantName);
        pressureMqttClient.setThirdThingsId(thirdThingsId);
        return new AsyncResult<>(pressureMqttClient);
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
//                    throw new RuntimeException(err.getResponseBodyAsString());
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)));
        return deviceTokenResMono.block();
    }

    @Async
    public void deviceSendData(PressureMqttClient pressureMqttClient, byte[] send, String type) {
        String sendTopic = new StringBuilder("/d2s/")
                .append(pressureMqttClient.getTenantName())
                .append("/").append(pressureMqttClient.getInfoModelName())
                .append("/").append(pressureMqttClient.getThirdThingsId())
                .append("/").append(type).toString();
        pressureMqttClient.getMqttClient().publishWith().topic(sendTopic).payload(send).send();
    }
}
