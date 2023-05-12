package com.zlg.pressurer2.service;

import com.zlg.pressurer2.common.GlobalDeviceList;
import com.zlg.pressurer2.common.GlobalWebClient;
import com.zlg.pressurer2.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class DeviceService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final HashMap<String, Integer> PUBLIC_INFO_MODEL = new HashMap<>();
    private static WebClient webClient = GlobalWebClient.getWebClient();
    private static final Integer TENANT_TOTAL = 10;
    @Resource
    private AsyncTaskService asyncTaskService;
    @Resource
    private GlobalDeviceList globalDeviceList;

    //初始化拿标准设备类型信息
    static {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("12345678");
        loginRequest.setType(1);
        loginRequest.setUsername("pressure1");

        //租户登录
        Mono<LoginRequest> req = Mono.just(loginRequest);
        Mono<LoginRes> loginResMono = webClient.post()
                .uri("/control/sessions/tenant-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req, LoginRequest.class)
                .retrieve()
                .bodyToMono(LoginRes.class);
        LoginRes loginRes = loginResMono.block();
        assert loginRes != null;
        String authorization = "Bearer " + loginRes.getToken();

        Mono<PublicInfoModelList> publicInfoModelListMono = webClient.get()
                .uri("/mapping-mgmt/info-models/public?current_page=1&names=invert can-common&page_size=20")
                .header("Authorization", authorization)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PublicInfoModelList.class);
        PublicInfoModelList publicInfoModelList = publicInfoModelListMono.block();
        assert publicInfoModelList != null;
        List<InfoModel> list = publicInfoModelList.getList();
        for (InfoModel infoModel : list) {
            PUBLIC_INFO_MODEL.put(infoModel.getName(), infoModel.getId());
        }
    }

    /**
     * 获取测试设备总数
     *
     * @param devType 设备类型
     */
    public Integer getDeviceTotal(String devType) throws ExecutionException, InterruptedException {
        globalDeviceList.clearAllDeviceInfoList();
        ArrayList<Future<DeviceInfoList>> futureList = new ArrayList<>();
        ArrayList<DeviceInfo> allDeviceInfoList = new ArrayList<>();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("12345678");
        loginRequest.setType(1);

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= TENANT_TOTAL; i++) {
            Future<DeviceInfoList> deviceInfoListFuture = asyncTaskService.getDeviceInfoList(devType, loginRequest, i,
                    PUBLIC_INFO_MODEL, webClient);
            futureList.add(deviceInfoListFuture);
        }
        for (Future<DeviceInfoList> deviceInfoListFuture : futureList) {
            DeviceInfoList deviceInfoList = deviceInfoListFuture.get();
            allDeviceInfoList.addAll(deviceInfoList.getList());
            globalDeviceList.setAllDeviceInfoList(allDeviceInfoList);
        }
        long endTime = System.currentTimeMillis();
        logger.info("{}个租户的设备总数为: {}", TENANT_TOTAL, allDeviceInfoList.size());
        logger.info("获取{}个租户设备列表共耗时: {}ms", TENANT_TOTAL, endTime - startTime);
        return  allDeviceInfoList.size();
    }

}
