package com.example.apiserver.domain.auth.controller;

import com.example.apiserver.domain.auth.dto.TokenResponseDto;
import com.example.apiserver.domain.auth.service.AuthService;
import com.example.apiserver.global.common.ApiResponse;
import com.example.apiserver.global.common.SuccessCode;
import com.example.apiserver.global.exception.CustomException;
import com.example.apiserver.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "토큰 재발급", description = "쿠키의 Refresh Token을 사용하여 새로운 토큰을 발급합니다.")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponseDto> refresh(
            // 쿠키에서 refresh_token 값을 꺼내오기
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN); // 쿠키가 없으면 에러
        }

        TokenResponseDto tokenDto = authService.refreshToken(refreshToken, response);

        return ApiResponse.success(SuccessCode.SUCCESS, tokenDto);
    }

    @Operation(summary = "로그인 성공 토큰 확인 (개발용)")
    @GetMapping("/token")
    public String showToken(@RequestParam String accessToken) {
        return "로그인 성공!\n\nAccess Token:\n" + accessToken;
    }
}
