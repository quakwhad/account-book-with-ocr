package com.example.apiserver.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${external-api.fastapi.base-url}")
    private String fastApiBaseUrl;

    @Bean
    public RestClient fastApiRestClient() {
        return RestClient.builder()
                .baseUrl(fastApiBaseUrl)
                .build();
    }
}
