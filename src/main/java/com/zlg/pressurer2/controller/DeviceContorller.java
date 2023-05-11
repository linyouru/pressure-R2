package com.zlg.pressurer2.controller;

import com.zlg.pressurer2.controller.model.ApiDevicesInfo;
import com.zlg.pressurer2.service.DeviceService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

@RestController
@Api(tags = "device")
public class DeviceContorller implements DeviceApi{

    @Resource
    private DeviceService deviceService;

    @Override
    public ResponseEntity<Void> addDevices(Integer deviceNumber, String deviceType) {
        return null;
    }

    @Override
    public ResponseEntity<ApiDevicesInfo> getDevices(String devType) {

        deviceService.getDeviceTotal(devType);

        return null;
    }
}
