package com.zlg.pressurer2.controller;

import com.zlg.pressurer2.helper.mqtt.MqttHelper;
import com.zlg.pressurer2.pojo.DeviceTokenRes;
import com.zlg.pressurer2.pojo.DeviceTokenResDataMqtt;
import com.zlg.pressurer2.pojo.LoginRequest;
import io.swagger.annotations.Api;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;



@RestController
@Api(tags = "test")
public class TestController implements TestApi {

    @Override
    public ResponseEntity<Void> login() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("linyouru");
        loginRequest.setPassword("lyr123456");
        loginRequest.setType(1);
        Mono<LoginRequest> req = Mono.just(loginRequest);

        String baseUrl = "http://192.168.24.10/v1";
        WebClient webClient = WebClient.create(baseUrl);
//        Mono<LoginRes> loginResMono = webClient.post()
//                .uri("/control/sessions/tenant-manager")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(req, LoginRequest.class)
//                .retrieve()
//                .bodyToMono(LoginRes.class);
//
//        LoginRes res = loginResMono.block();

        //获取设备token
        String parentsJson = "{\"devices\":[{\"devid\":\"invent_05\",\"devtype\":\"invert\"}],\"password\":\"ASfa@#regksajFAwvI)\",\"username\":\"invert\"}";
//        String parentsJson = "{\"devices\":[{\"devid\":\"device_invert_1_500\",\"devtype\":\"invert\"}],\"password\":\"ASfa@#regksajFAwvI)\",\"username\":\"invert\"}";
        Mono<DeviceTokenRes> deviceTokenResMono = webClient.post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(parentsJson)
                .retrieve()
                .bodyToMono(DeviceTokenRes.class)
                .doOnError(WebClientResponseException.class, err -> {
                    System.out.println("发生错误：" + err.getRawStatusCode() + " "
                            + err.getResponseBodyAsString());
                    throw new RuntimeException(err.getResponseBodyAsString());
                });
        DeviceTokenRes deviceTokenRes = deviceTokenResMono.block();
        DeviceTokenResDataMqtt mqtt = deviceTokenRes.getData().getMqtt();

        System.out.println("get device token: " + deviceTokenRes.getData().getToken());
        String serverUri = "tcp://" + mqtt.getHost() + ":" + mqtt.getPort();
        String clientId = "invert:invent_05";
        try {
            MqttClient mqttClient = MqttHelper.getMqttClient(serverUri.trim(), clientId, clientId, deviceTokenRes.getData().getToken());
            MqttMessage mqttMessage = new MqttMessage("设备上线".getBytes());
            //开发调试时Qos设为0，因为在虚拟机里收不到服务端响应
            mqttMessage.setQos(0);
            mqttClient.publish("/d2s/linyouru/invert/invent_05/online", mqttMessage);
            mqttClient.disconnect();
            mqttClient.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

        System.out.println("end");
        return ResponseEntity.ok().body(null);
    }

}
