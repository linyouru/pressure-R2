package com.zlg.pressurer2.controller;

import com.zlg.pressurer2.common.GlobalDeviceList;
import com.zlg.pressurer2.controller.model.ApiDevicesInfo;
import com.zlg.pressurer2.pojo.DeviceInfo;
import com.zlg.pressurer2.service.AsyncTaskService;
import com.zlg.pressurer2.service.DeviceService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@Api(tags = "device")
public class DeviceContorller implements DeviceApi {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private DeviceService deviceService;
    @Resource
    private GlobalDeviceList globalDeviceList;

    @Override
    public ResponseEntity<Void> addDevices(Integer deviceNumber, String deviceType) {
        ArrayList<DeviceInfo> allDeviceInfoList = globalDeviceList.getAllDeviceInfoList();
        logger.info("当前全部租户设备总数: {}",allDeviceInfoList.size());
        return null;
    }

    @Override
    public ResponseEntity<ApiDevicesInfo> getDevices(String devType) {

        try {
            Integer deviceTotal = deviceService.getDeviceTotal(devType);
            ApiDevicesInfo res = new ApiDevicesInfo();
            res.setTotal(deviceTotal);
            res.setDeviceType(devType);
            return ResponseEntity.ok().body(res);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("getDevices error: ",e);
            throw new RuntimeException(e);
        }
    }
}
