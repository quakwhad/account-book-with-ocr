package com.example.apiserver.domain.ledger.dto;

import com.example.apiserver.domain.ledger.entity.LedgerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record LedgerRequestDto(
    @NotNull(message = "금액은 필수 입력 사항입니다.")
    @Positive(message = "금액은 양수여야 합니다.")
    Long amount,

    @NotBlank(message = "카테고리는 필수 입력 사항입니다.")
    String category,

    String description,

    @NotNull(message = "유형(수입/지출)은 필수 선택 사항입니다.")
    LedgerType type,

    @NotNull(message = "날짜는 필수 입력 사항입니다.")
    LocalDate date
) {
}
