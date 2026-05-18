package com.example.apiserver.global.common;

import com.example.apiserver.global.exception.ErrorCode;

public record ApiResponse<T>(
    String code,
    String message,
    T data
) {
    public static <T> ApiResponse<T> success(SuccessCode successCode, T data) {
        return new ApiResponse<>(successCode.getCode(), successCode.getMessage(), data);
    }

    public static <T> ApiResponse<T> success(SuccessCode successCode) {
        return new ApiResponse<>(successCode.getCode(), successCode.getMessage(), null);
    }

    public static ApiResponse<Object> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static ApiResponse<Object> error(ErrorCode errorCode, Object errors) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), errors);
    }
}
