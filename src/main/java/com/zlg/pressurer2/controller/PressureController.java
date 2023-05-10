package com.zlg.pressurer2.controller;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public class PressureController implements PressureApi{

    @Override
    public ResponseEntity<Void> pressureStart(Integer deviceNumber, String deviceType, Integer period, String topic, String data, Integer onlineTime) {
        return null;
    }

    @Override
    public ResponseEntity<Void> pressureStop() {
        return null;
    }
}
