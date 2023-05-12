package com.zlg.pressurer2.controller;

import com.zlg.pressurer2.service.PressureService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

@RestController
@Api(tags = "pressure")
public class PressureController implements PressureApi {

    @Resource
    private PressureService pressureService;

    @Override
    public ResponseEntity<Void> pressureStart(Integer deviceNumber, String deviceType, Integer period, String topic, String data, Integer onlineTime) {
        try {
            pressureService.pressureStart(deviceNumber, deviceType, period, topic, data, onlineTime);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok().body(null);
    }

    @Override
    public ResponseEntity<Void> pressureStop() {
        return null;
    }
}
