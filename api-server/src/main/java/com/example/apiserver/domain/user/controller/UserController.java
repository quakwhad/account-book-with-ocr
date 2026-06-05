package com.example.apiserver.domain.user.controller;

import com.example.apiserver.domain.user.dto.UserRequestDto;
import com.example.apiserver.domain.user.dto.UserResponseDto;
import com.example.apiserver.domain.user.service.UserService;
import com.example.apiserver.global.common.ApiResponse;
import com.example.apiserver.global.common.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto) {
        return ApiResponse.success(SuccessCode.CREATED, userService.createUser(dto));
    }

    @Operation(summary = "사용자 단건 조회")
    @GetMapping("/{id}")
    public ApiResponse<UserResponseDto> getUser(@PathVariable Long id) {
        return ApiResponse.success(SuccessCode.SUCCESS, userService.getUser(id));
    }

    @Operation(summary = "사용자 전체 조회")
    @GetMapping
    public ApiResponse<List<UserResponseDto>> getAllUsers() {
        return ApiResponse.success(SuccessCode.SUCCESS, userService.getAllUsers());
    }

    @Operation(summary = "사용자 수정")
    @PutMapping("/{id}")
    public ApiResponse<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto dto) {
        return ApiResponse.success(SuccessCode.SUCCESS, userService.updateUser(id, dto));
    }

    @Operation(summary = "사용자 삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success(SuccessCode.DELETE_SUCCESS);
    }
}
