package com.example.apiserver.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
    @NotBlank(message = "이름은 필수 입력 사항입니다.")
    String name
) {
}
