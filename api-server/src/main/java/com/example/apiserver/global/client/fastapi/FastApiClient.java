package com.example.apiserver.global.client.fastapi;

import com.example.apiserver.global.exception.CustomException;
import com.example.apiserver.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class FastApiClient {

    private final RestClient restClient;

    public FastApiClient(@Qualifier("fastApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public void uploadReceipt(Long userId, MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("userId", userId);

        try {
            restClient.post()
                    .uri("/api/v2/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        log.error("FastAPI error status: {}", response.getStatusCode());
                        throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
                    })
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            log.error("FastAPI server is not reachable: {}", e.getMessage());
            throw new CustomException(ErrorCode.FASTAPI_SERVER_ERROR);
        }
    }
}