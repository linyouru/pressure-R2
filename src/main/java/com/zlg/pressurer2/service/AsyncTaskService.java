package com.zlg.pressurer2.service;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.zlg.pressurer2.helper.mqtt.MqttHelper;
import com.zlg.pressurer2.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.concurrent.Future;

@Component
public class AsyncTaskService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MqttHelper mqttHelper;


    @Async
    public Future<PressureMqttClient> deviceOnline(String deviceType, String thirdThingsId, String tenantName, String parentsJson, WebClient webClient) {
        DeviceTokenRes deviceTokenRes = getDeviceTokenRes(parentsJson, webClient, thirdThingsId);
        if (!deviceTokenRes.getResult()) {
            return null;
        }
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
    private DeviceTokenRes getDeviceTokenRes(String parentsJson, WebClient webClient, String thirdThingsId) {
        Mono<DeviceTokenRes> deviceTokenResMono = webClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(parentsJson)
                .retrieve()
//                .onStatus(HttpStatus::is4xxClientError, resp -> {
//                    logger.error("获取token客户端请求异常[{}],error: {}", resp.statusCode().value(), resp.statusCode().getReasonPhrase());
//                    return Mono.error(new RuntimeException("客户端请求异常"));
//                })
//                .onStatus(HttpStatus::is5xxServerError, resp -> {
//                    logger.error("获取token服务端异常[{}],error: {}", resp.statusCode().value(), resp.statusCode().getReasonPhrase());
//                    return Mono.error(new RuntimeException("服务端异常"));
//                })
                .bodyToMono(DeviceTokenRes.class)
                .doOnError(WebClientResponseException.class, err -> {
                    logger.error("获取设备[{}]登录token发生错误：" + err.getRawStatusCode() + " " + err.getResponseBodyAsString(Charset.defaultCharset()), thirdThingsId);
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                .onErrorReturn(new DeviceTokenRes());
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
