package com.example.apiserver.domain.ledger.controller;

import com.example.apiserver.domain.ledger.dto.LedgerResponseDto;
import com.example.apiserver.domain.ledger.service.LedgerService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin - Ledger", description = "관리자용 가계부 내역 관리 API")
@RestController
@RequestMapping("/api/v1/admin/ledgers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROOT')")
public class AdminLedgerController {

    private final LedgerService ledgerService;

    @Operation(summary = "[관리자] 전체 가계부 내역 조회", description = "시스템의 모든 가계부 내역을 조회합니다.")
    @GetMapping("/all")
    public ApiResponse<Page<LedgerResponseDto>> getAllLedgers(
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(SuccessCode.SUCCESS, ledgerService.getAllLedgersForAdmin(pageable));
    }

    @Operation(summary = "[관리자] 특정 사용자 가계부 조회", description = "특정 사용자의 가계부 내역을 조회합니다.")
    @GetMapping("/users/{userId}")
    public ApiResponse<Page<LedgerResponseDto>> getUserLedgers(
            @PathVariable Long userId,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(SuccessCode.SUCCESS, ledgerService.getUserLedgersForAdmin(userId, pageable));
    }
    
    @Operation(summary = "[관리자] 특정 가계부 내역 조회", description = "ID를 통해 특정 가계부 내역을 상세 조회합니다.")
    @GetMapping("/{id}")
    public ApiResponse<LedgerResponseDto> getLedger(@PathVariable Long id) {
        return ApiResponse.success(SuccessCode.SUCCESS, ledgerService.getLedger(id));
    }
}
