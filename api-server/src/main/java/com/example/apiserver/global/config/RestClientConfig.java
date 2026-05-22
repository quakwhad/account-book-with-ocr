package com.example.apiserver.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

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
        // text/html 응답도 JSON으로 처리할 수 있도록 커스텀 컨버터 생성
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_HTML
        ));

        return RestClient.builder()
                .messageConverters(converters -> {
                    // 기본 컨버터 목록에 커스텀 컨버터를 추가
                    converters.add(0, converter);
                })
                .build();
    }
}