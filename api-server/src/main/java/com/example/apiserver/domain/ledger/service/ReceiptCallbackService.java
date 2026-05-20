package com.example.apiserver.domain.ledger.service;

import com.example.apiserver.domain.ledger.dto.LedgerRequestDto;
import com.example.apiserver.domain.ledger.dto.LedgerResponseDto;
import com.example.apiserver.global.client.fastapi.dto.ReceiptAnalysisResponseDto;
import com.example.apiserver.global.exception.CustomException;
import com.example.apiserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceiptCallbackService {

    private final LedgerService ledgerService;

    @Value("${external-api.fastapi.callback-secret}")
    private String callbackSecret;

    @Transactional
    public LedgerResponseDto processReceiptCallback(String secret, ReceiptAnalysisResponseDto dto) {
        validateCallbackSecret(secret);

        LedgerRequestDto ledgerRequestDto = new LedgerRequestDto(
                dto.amount(),
                dto.category(),
                dto.description(),
                dto.type(),
                dto.date()
        );

        return ledgerService.createLedger(dto.userId(), ledgerRequestDto);
    }

    private void validateCallbackSecret(String secret) {
        if (secret == null || !secret.equals(callbackSecret)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }
}