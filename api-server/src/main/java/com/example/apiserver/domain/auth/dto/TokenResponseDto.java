package com.example.apiserver.domain.auth.dto;

import lombok.Builder;

@Builder
public record TokenResponseDto(
    String accessToken,
    String refreshToken,
    String grantType
) {
    public static TokenResponseDto of(String accessToken, String refreshToken) {
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType("Bearer")
                .build();
    }
}
