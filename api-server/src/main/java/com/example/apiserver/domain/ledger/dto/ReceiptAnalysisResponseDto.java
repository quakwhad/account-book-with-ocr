package com.example.apiserver.domain.ledger.dto;

import com.example.apiserver.domain.ledger.entity.LedgerType;

import java.time.LocalDate;

public record ReceiptAnalysisResponseDto(
    Long userId,
    Long amount,
    String category,
    String description,
    LedgerType type,
    LocalDate date
) {
}
