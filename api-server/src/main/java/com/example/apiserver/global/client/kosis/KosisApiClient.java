package com.example.apiserver.global.client.kosis;

import com.example.apiserver.global.client.kosis.dto.KosisStatisticResponseDto;
import com.example.apiserver.global.exception.CustomException;
import com.example.apiserver.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class KosisApiClient {

    private final RestClient restClient;

    @Value("${external-api.kosis.url}")
    private String kosisApiUrl;

    public KosisApiClient(@Qualifier("kosisRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<KosisStatisticResponseDto> getNationalAverageExpenditure() {
        try {
            KosisStatisticResponseDto[] response = restClient.get()
                    .uri(kosisApiUrl)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, res) -> {
                        log.error("KOSIS API error status: {}", res.getStatusCode());
                        throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
                    })
                    .body(KosisStatisticResponseDto[].class);

            if (response == null || response.length == 0) {
                log.warn("KOSIS API 응답이 비어있습니다.");
                throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
            }

            return Arrays.asList(response);

        } catch (ResourceAccessException e) {
            log.error("KOSIS API 서버에 연결할 수 없습니다: {}", e.getMessage());
            throw new CustomException(ErrorCode.KOSIS_SERVER_ERROR);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("KOSIS API 호출 중 예측하지 못한 오류 발생: {}", e.getMessage());
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}