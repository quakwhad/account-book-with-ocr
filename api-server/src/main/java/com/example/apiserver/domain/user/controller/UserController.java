package com.example.apiserver.domain.user.controller;

import com.example.apiserver.domain.user.dto.UserRequestDto;
import com.example.apiserver.domain.user.dto.UserResponseDto;
import com.example.apiserver.domain.user.service.UserService;
import com.example.apiserver.global.security.principal.UserPrincipal;
import com.example.apiserver.global.common.ApiResponse;
import com.example.apiserver.global.common.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User - Account", description = "사용자 계정 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<UserResponseDto> getMyInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ApiResponse.success(SuccessCode.SUCCESS, userService.getUser(userPrincipal.getUserId()));
    }

    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
    @PutMapping("/me")
    public ApiResponse<UserResponseDto> updateMyInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        return ApiResponse.success(SuccessCode.SUCCESS, userService.updateUser(userPrincipal.getUserId(), userRequestDto));
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자가 탈퇴를 요청합니다.")
    @DeleteMapping("/me")
    public ApiResponse<Void> deleteMe(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        userService.deleteUser(userPrincipal.getUserId());
        return ApiResponse.success(SuccessCode.DELETE_SUCCESS);
    }
}
