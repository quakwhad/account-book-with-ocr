package com.example.apiserver.global.client.kosis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KosisStatisticResponseDto(
        @JsonProperty("PRD_DE")
        String prdDe,  // 수록기간 (예: 202504)

        @JsonProperty("C1_NM")
        String c1Nm,   // 가구원 수 (예: 1인)

        @JsonProperty("C2_NM")
        String c2Nm,   // 항목명 (예: 01.식료품 · 비주류음료)

        @JsonProperty("DT")
        String dt,     // 데이터 값 (주의: "223595.759" 처럼 소수점 포함)

        @JsonProperty("UNIT_NM")
        String unitNm  // 단위 (예: 원)
) {}