package com.example.apiserver.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    ROOT("ROLE_ROOT");

    private final String key;
}
