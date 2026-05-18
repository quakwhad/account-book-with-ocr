package com.example.apiserver.domain.ledger.dto;

import com.example.apiserver.domain.ledger.entity.Ledger;
import com.example.apiserver.domain.ledger.entity.LedgerType;

import java.time.LocalDate;

public record LedgerResponseDto(
    Long id,
    Long userId,
    Long amount,
    String category,
    String description,
    LedgerType type,
    LocalDate date
) {
    public static LedgerResponseDto from(Ledger ledger) {
        return new LedgerResponseDto(
            ledger.getId(),
            ledger.getUser().getId(),
            ledger.getAmount(),
            ledger.getCategory(),
            ledger.getDescription(),
            ledger.getType(),
            ledger.getDate()
        );
    }
}
