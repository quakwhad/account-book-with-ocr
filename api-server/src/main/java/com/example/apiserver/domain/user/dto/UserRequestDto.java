package com.example.apiserver.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
        @NotBlank String name,
        @NotBlank @Email String email
) {}
