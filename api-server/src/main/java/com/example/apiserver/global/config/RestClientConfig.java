package com.example.apiserver.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${external-api.fastapi.base-url}")
    private String fastApiBaseUrl;

    // FastAPI 전용 RestClient
    @Bean(name = "fastApiRestClient")
    public RestClient fastApiRestClient() {
        return RestClient.builder()
                .baseUrl(fastApiBaseUrl)
                .build();
    }

    // KOSIS API 전용 RestClient
    @Bean(name = "kosisRestClient")
    public RestClient kosisRestClient() {
        return RestClient.builder()
                .build();
    }
}