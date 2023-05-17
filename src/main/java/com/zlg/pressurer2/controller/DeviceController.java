package com.zlg.pressurer2.controller;

import com.zlg.pressurer2.common.GlobalDeviceList;
import com.zlg.pressurer2.controller.model.ApiDevicesInfo;
import com.zlg.pressurer2.pojo.DeviceInfo;
import com.zlg.pressurer2.service.DeviceService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

@RestController
@Api(tags = "device")
public class DeviceController implements DeviceApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private DeviceService deviceService;
    @Resource
    private GlobalDeviceList globalDeviceList;

    @Override
    public ResponseEntity<Void> addDevices(Integer deviceNumber, String deviceType) {
        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<ApiDevicesInfo> getDevices(String devType) {
        return ResponseEntity.ok(null);
//        try {
//            Integer deviceTotal = deviceService.getDeviceTotal(devType);
//            ApiDevicesInfo res = new ApiDevicesInfo();
//            res.setTotal(deviceTotal);
//            res.setDeviceType(devType);
//            return ResponseEntity.ok().body(res);
//        } catch (ExecutionException | InterruptedException e) {
//            logger.error("getDevices error: ",e);
//            throw new RuntimeException(e);
//        }
    }
}
