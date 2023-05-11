package com.zlg.pressurer2.service;

import com.zlg.pressurer2.pojo.DeviceInfoList;
import com.zlg.pressurer2.pojo.LoginRequest;
import com.zlg.pressurer2.pojo.LoginRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.concurrent.Future;

@Component
public class AsyncTaskService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Async
//    public void asyncTest(int i){
//        logger.info("线程ID：" + Thread.currentThread().getId() +" 线程名字：" +Thread.currentThread().getName()+" 获取租户pressure{}的设备列表", i);
//    }

    @Async
    public Future<DeviceInfoList> asyncGetDeviceInfoList(String devType, LoginRequest loginRequest, int i,
                                                         HashMap<String, Integer> publicInfoModel, WebClient webClient) {
        loginRequest.setUsername("pressure" + i);
        LoginRes loginRes = apiTenantLogin(loginRequest,webClient);

        String authorization = "Bearer " + loginRes.getToken();

        Integer tenantId = loginRes.getTenant_id();
        Integer infoModelId = publicInfoModel.get(devType);
        DeviceInfoList deviceInfoList = apiGetDeviceInfoList(authorization, tenantId, infoModelId,webClient);
        logger.info("线程ID：" + Thread.currentThread().getId() +" 线程名字：" +Thread.currentThread().getName()+" 获取租户pressure{}的设备列表", i);
        return new AsyncResult<>(deviceInfoList);
    }

    /**
     * 获取租户设备列表
     *
     * @param authorization token
     * @param tenantId      租户id
     * @param infoModelId   设备类型id
     * @return
     */
    private DeviceInfoList apiGetDeviceInfoList(String authorization, Integer tenantId, Integer infoModelId,WebClient webClient) {
        Mono<DeviceInfoList> deviceInfoListMono = webClient.get()
                .uri("/things/tenants/" + tenantId + "/things?current_page=1&info_model_ids=" + infoModelId + "&page_size=10000")
                .header("Authorization", authorization)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(DeviceInfoList.class);
        return deviceInfoListMono.block();
    }

    /**
     * 租户登录
     *
     * @param loginRequest 登录请求参数
     * @return
     */
    private LoginRes apiTenantLogin(LoginRequest loginRequest,WebClient webClient) {
        Mono<LoginRequest> req = Mono.just(loginRequest);
        Mono<LoginRes> loginResMono = webClient.post()
                .uri("/control/sessions/tenant-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req, LoginRequest.class)
                .retrieve()
                .bodyToMono(LoginRes.class);
        return loginResMono.block();
    }


}
