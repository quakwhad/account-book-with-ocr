package com.example.apiserver.domain.ledger.controller;

import com.example.apiserver.domain.ledger.dto.LedgerResponseDto;
import com.example.apiserver.domain.ledger.service.ReceiptCallbackService;
import com.example.apiserver.global.client.fastapi.dto.ReceiptAnalysisResponseDto;
import com.example.apiserver.global.common.ApiResponse;
import com.example.apiserver.global.common.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Internal - Receipt Callback", description = "FastAPI 분석 결과 수신용 내부 API")
@RestController
@RequestMapping("/api/v1/internal/receipt")
@RequiredArgsConstructor
public class ReceiptCallbackController {

    private final ReceiptCallbackService receiptCallbackService;

    @Operation(summary = "FastAPI 분석 결과 수신 (Callback)", description = "FastAPI 서버에서 분석된 결과를 수신하여 가계부 내역을 생성합니다.")
    @PostMapping("/callback")
    public ApiResponse<LedgerResponseDto> receiveReceiptCallback(
            @RequestHeader(value = "X-Callback-Secret", required = false) String secret,
            @RequestBody ReceiptAnalysisResponseDto receiptAnalysisResponseDto) {

        LedgerResponseDto response = receiptCallbackService.processReceiptCallback(secret, receiptAnalysisResponseDto);
        return ApiResponse.success(SuccessCode.SUCCESS, response);
    }
}