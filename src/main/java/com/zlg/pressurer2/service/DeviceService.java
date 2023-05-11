package com.zlg.pressurer2.service;

import com.zlg.pressurer2.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class DeviceService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HashMap<String,Integer> publicInfoModel = new HashMap<>();

    /**
     * 获取测试设备总数
     * @param devType 设备类型
     */
    public void getDeviceTotal (String devType){
        ArrayList<DeviceInfo> allDeviceInfoList = new ArrayList<>();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("12345678");
        loginRequest.setType(1);

        String baseUrl = "http://192.168.24.91/v1";
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5*1024*1024)).build();

        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= 1; i++) {
            //租户登录
            loginRequest.setUsername("pressure"+i);
            Mono<LoginRequest> req = Mono.just(loginRequest);
            Mono<LoginRes> loginResMono = webClient.post()
                    .uri("/control/sessions/tenant-manager")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(req, LoginRequest.class)
                    .retrieve()
                    .bodyToMono(LoginRes.class);
            LoginRes loginRes = loginResMono.block();

            String authorization = "Bearer " + loginRes.getToken();

            if(i == 1){
                //拿标准设备类型信息，只用拿一次
                Mono<PublicInfoModelList> publicInfoModelListMono = webClient.get()
                        .uri("/mapping-mgmt/info-models/public?current_page=1&names=invert can-common&page_size=20")
                        .header("Authorization", authorization)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(PublicInfoModelList.class);
                PublicInfoModelList publicInfoModelList = publicInfoModelListMono.block();
                List<InfoModel> list = publicInfoModelList.getList();
                for (InfoModel infoModel : list) {
                    publicInfoModel.put(infoModel.getName(),infoModel.getId());
                }
            }

            //获取租户设备列表
            Integer tenantId = loginRes.getTenant_id();
            Integer infoModelId = publicInfoModel.get(devType);
            Mono<DeviceInfoList> deviceInfoListMono = webClient.get()
                    .uri("/things/tenants/" + tenantId + "/things?current_page=1&info_model_ids=" + infoModelId + "&page_size=10000")
                    .header("Authorization", authorization)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(DeviceInfoList.class);
            DeviceInfoList deviceInfoList = deviceInfoListMono.block();
            allDeviceInfoList.addAll(deviceInfoList.getList());
        }
        long endTime = System.currentTimeMillis();
        logger.info("allDeviceInfoList size: {}",allDeviceInfoList.size());
        logger.info("time: {}",endTime-startTime);
    }

}
