package com.zlg.pressurer2.controller;

import com.zlg.pressurer2.controller.model.ApiDevicesInfo;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

public class DeviceContorller implements DeviceApi{

    @Override
    public ResponseEntity<Void> addDevices(Integer deviceNumber, String deviceType) {
        return null;
    }

    @Override
    public ResponseEntity<ApiDevicesInfo> getDevices(String devType) {
        return null;
    }
}
