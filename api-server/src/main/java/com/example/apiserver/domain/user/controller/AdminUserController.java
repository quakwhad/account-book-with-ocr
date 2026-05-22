package com.example.apiserver.domain.user.controller;

import com.example.apiserver.domain.user.dto.UserResponseDto;
import com.example.apiserver.domain.user.service.UserService;
import com.example.apiserver.global.common.ApiResponse;
import com.example.apiserver.global.common.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin - User", description = "관리자용 사용자 관리 API")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROOT')")
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "[관리자] 전체 사용자 조회", description = "시스템의 모든 사용자를 조회합니다.")
    @GetMapping
    public ApiResponse<Page<UserResponseDto>> getAllUsers(
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(SuccessCode.SUCCESS, userService.getAllUsers(pageable));
    }

    @Operation(summary = "[관리자] 특정 사용자 조회", description = "ID를 통해 특정 사용자를 상세 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<UserResponseDto> getUser(@PathVariable Long id) {
        return ApiResponse.success(SuccessCode.SUCCESS, userService.getUser(id));
    }

    @Operation(summary = "[관리자] 사용자 삭제", description = "특정 사용자를 강제 탈퇴 처리합니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(SuccessCode.DELETE_SUCCESS);
    }
}
