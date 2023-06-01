package com.zlg.pressurer2.common;

import org.springframework.web.reactive.function.client.WebClient;

public class GlobalWebClient {

    private final static WebClient WEB_CLIENT;
    private static final String BASE_URL = "http://192.168.24.91/v1";

    static {
        WEB_CLIENT = WebClient
                .builder()
                .baseUrl(BASE_URL)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(5 * 1024 * 1024))
                .build();
    }

    public static WebClient getWebClient() {
        return WEB_CLIENT;
    }

}
