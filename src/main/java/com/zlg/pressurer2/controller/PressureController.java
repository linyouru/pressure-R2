package com.zlg.pressurer2.controller;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@Api(tags = "pressure")
public class PressureController implements PressureApi{

    @Override
    public ResponseEntity<Void> pressureStart(Integer deviceNumber, String deviceType, Integer period, String topic, String data,
                                              Integer onlineTime) {


        return null;
    }

    @Override
    public ResponseEntity<Void> pressureStop() {
        return null;
    }
}
