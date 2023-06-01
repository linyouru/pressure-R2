package com.zlg.pressurer2.controller;

import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.zlg.pressurer2.common.GlobalMqttClientList;
import com.zlg.pressurer2.controller.model.ApiBaseResp;
import com.zlg.pressurer2.pojo.PressureMqttClient;
import com.zlg.pressurer2.service.PressureService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@RestController
@Api(tags = "pressure")
public class PressureController implements PressureApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private PressureService pressureService;

    @Override
    public ResponseEntity<ApiBaseResp> deviceOnline(Integer deviceNumber, String deviceType, Integer part, Integer rest, Integer startUserIndex, Integer startDeviceIndex) {

        try {
            pressureService.deviceOnline(deviceNumber, deviceType, part, rest, startUserIndex, startDeviceIndex);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<ApiBaseResp> pressureStart(Integer deviceNumber, String deviceType, Integer part, Integer rest, Integer period, String topic, String data, Integer startUserIndex, Integer startDeviceIndex) {
        try {
            pressureService.deviceOnline(deviceNumber, deviceType, part, rest, startUserIndex, startDeviceIndex);
            pressureService.pressureStart(period, topic, data);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(new ApiBaseResp().message("success"));
    }

    @Override
    public ResponseEntity<ApiBaseResp> pressureStop() {
        pressureService.pressureStop();
        ArrayList<PressureMqttClient> mqttClientList = GlobalMqttClientList.mqttClientList;
        if (null != mqttClientList) {
            logger.debug("当前mqtt client数: {}", mqttClientList.size());
            for (PressureMqttClient pressureMqttClient : mqttClientList) {
                if(null != pressureMqttClient){
                    Mqtt3AsyncClient mqttClient = pressureMqttClient.getMqttClient();
                    if (null != mqttClient) {
                        mqttClient.disconnect();
                    }
                }
            }
            GlobalMqttClientList.mqttClientList.clear();
        }
        return ResponseEntity.ok(new ApiBaseResp().message("success"));
    }
}
