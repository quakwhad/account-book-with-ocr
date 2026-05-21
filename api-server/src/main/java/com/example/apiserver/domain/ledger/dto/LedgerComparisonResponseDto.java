package com.example.apiserver.domain.ledger.dto;

import lombok.Builder;

@Builder
public record LedgerComparisonResponseDto(
        long myTotalExpenditure,
        long nationalAverageExpenditure,
        long differenceAmount,
        String comparisonMessage
) {}