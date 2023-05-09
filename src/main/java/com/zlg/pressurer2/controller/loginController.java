package com.zlg.pressurer2.controller;

import com.zlg.pressurer2.controller.model.ApiLoginRequest;
import com.zlg.pressurer2.pojo.LoginRequest;
import com.zlg.pressurer2.pojo.LoginRes;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@Api(tags = "auth", description = "授权认证相关接口")
public class loginController implements AuthApi{
    @Override
    public ResponseEntity<Void> login(Boolean rememberMe, ApiLoginRequest body) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("linyouru");
        loginRequest.setPassword("lyr123456");
        loginRequest.setType(1);
        Mono<LoginRequest> req = Mono.just(loginRequest);

        String baseUrl = "http://192.168.24.10/v1";
        WebClient webClient = WebClient.create(baseUrl);
        Mono<LoginRes> loginResMono = webClient.post()
                .uri("/control/sessions/tenant-manager")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req, LoginRequest.class)
                .retrieve()
                .bodyToMono(LoginRes.class);

        System.out.println(1);
        LoginRes res = loginResMono.block();
        return ResponseEntity.ok().body(null);
    }

    @Override
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().body(null);
    }
}
