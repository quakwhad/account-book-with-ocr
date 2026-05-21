package com.example.apiserver.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E001", "올바르지 않은 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "E002", "허용되지 않은 HTTP 메서드입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "E003", "존재하지 않는 리소스입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E004", "서버 내부 오류가 발생했습니다."),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "E005", "외부 API 연동 중 문제가 발생했습니다."),
    FASTAPI_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E006", "FastAPI 서버를 이용할 수 없습니다."),
    KOSIS_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E007", "KOSIS API 서버를 이용할 수 없습니다."),

    // Ledger
    LEDGER_NOT_FOUND(HttpStatus.NOT_FOUND, "L001", "가계부 내역을 찾을 수 없습니다."),
    LEDGER_DUPLICATED(HttpStatus.CONFLICT, "L002", "이미 존재하는 가계부 내역입니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    USER_DUPLICATED(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자입니다."),

    // Auth
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A001", "로그인에 실패했습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "만료된 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A004", "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "A005", "권한이 없습니다."); // 예시 추가됨

    private final HttpStatus status;
    private final String code;
    private final String message;
}