package com.example.apiserver.domain.auth.controller;

import com.example.apiserver.domain.auth.dto.TokenResponseDto;
import com.example.apiserver.domain.auth.service.AuthService;
import com.example.apiserver.global.common.ApiResponse;
import com.example.apiserver.global.common.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급합니다.")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponseDto> refresh(@RequestHeader("Authorization-Refresh") String refreshToken) {
        return ApiResponse.success(SuccessCode.SUCCESS, authService.refreshToken(refreshToken));
    }

    @Operation(summary = "로그인 성공 토큰 확인 (개발용)")
    @GetMapping("/token")
    public String showToken(@RequestParam String accessToken, @RequestParam String refreshToken) {
        return "로그인 성공!\n\nAccess Token:\n" + accessToken + "\n\nRefresh Token:\n" + refreshToken;
    }
}
