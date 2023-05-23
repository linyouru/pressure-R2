package com.zlg.pressurer2.controller;

import com.zlg.pressurer2.common.GlobalMqttClientList;
import com.zlg.pressurer2.controller.model.ApiBaseResp;
import com.zlg.pressurer2.pojo.PressureMqttClient;
import com.zlg.pressurer2.service.PressureService;
import io.swagger.annotations.Api;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@RestController
@Api(tags = "pressure")
public class PressureController implements PressureApi {

    @Resource
    private PressureService pressureService;

    @Override
    public ResponseEntity<ApiBaseResp> deviceOnline(Integer deviceNumber, String deviceType, Integer part, Integer rest, Integer startUserIndex, Integer startDeviceIndex) {

        try {
            pressureService.deviceOnline(deviceNumber, deviceType, part, rest,startUserIndex,startDeviceIndex);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<ApiBaseResp> pressureStart(Integer deviceNumber, String deviceType, Integer part, Integer rest, Integer period, String topic, String data, Integer startUserIndex, Integer startDeviceIndex) {
        try {
            pressureService.pressureStart(period, topic, data);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(new ApiBaseResp().message("success"));
    }

    @Override
    public ResponseEntity<ApiBaseResp> pressureStop() {
        ArrayList<PressureMqttClient> mqttClientList = GlobalMqttClientList.mqttClientList;
        pressureService.pressureStop();
        if (null != mqttClientList) {
            for (PressureMqttClient pressureMqttClient : mqttClientList) {
                MqttClient mqttClient = pressureMqttClient.getMqttClient();
                try {
                    if (null != mqttClient) {
                        mqttClient.disconnect();
                        mqttClient.close();
                    }
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }
            GlobalMqttClientList.mqttClientList.clear();
        }
        return ResponseEntity.ok(new ApiBaseResp().message("success"));
    }
}
