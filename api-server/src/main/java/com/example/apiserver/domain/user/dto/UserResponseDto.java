package com.example.apiserver.domain.user.dto;

import com.example.apiserver.domain.user.entity.User;

public record UserResponseDto(
        Long id,
        String name,
        String email
) {
    public static UserResponseDto from(User user) {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail());
    }
}
