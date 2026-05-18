package com.example.apiserver.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    SUCCESS(HttpStatus.OK, "S001", "요청이 성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "S002", "리소스가 성공적으로 생성되었습니다."),
    DELETE_SUCCESS(HttpStatus.OK, "S003", "리소스가 성공적으로 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
