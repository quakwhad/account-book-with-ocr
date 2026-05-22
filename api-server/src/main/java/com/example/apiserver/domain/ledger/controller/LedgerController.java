package com.example.apiserver.domain.ledger.controller;

import com.example.apiserver.domain.ledger.dto.*;
import com.example.apiserver.domain.ledger.service.LedgerService;
import com.example.apiserver.global.security.principal.UserPrincipal;
import com.example.apiserver.global.common.ApiResponse;
import com.example.apiserver.global.common.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User - Ledger", description = "사용자용 가계부 내역 관리 API")
@RestController
@RequestMapping("/api/v1/ledgers")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @Operation(summary = "가계부 내역 생성", description = "새로운 지출/수입 내역을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LedgerResponseDto> createLedger(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody LedgerRequestDto request) {
        return ApiResponse.success(SuccessCode.CREATED, ledgerService.createLedger(userPrincipal.getUserId(), request));
    }

    @Operation(summary = "가계부 내역 단건 조회", description = "ID를 통해 특정 가계부 내역을 조회합니다.")
    @GetMapping("/{id}")
    @PreAuthorize("@ledgerService.isOwner(principal.userId, #id)")
    public ApiResponse<LedgerResponseDto> getLedger(@PathVariable Long id) {
        return ApiResponse.success(SuccessCode.SUCCESS, ledgerService.getLedger(id));
    }

    @Operation(summary = "나의 가계부 내역 조회", description = "현재 로그인한 사용자의 가계부 내역을 페이징 처리하여 조회합니다.")
    @GetMapping
    public ApiResponse<Page<LedgerResponseDto>> getMyLedgers(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.success(SuccessCode.SUCCESS, ledgerService.getMyLedgers(userPrincipal.getUserId(), pageable));
    }

    @Operation(summary = "가계부 내역 수정", description = "기존 가계부 내역을 수정합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("@ledgerService.isOwner(principal.userId, #id)")
    public ApiResponse<LedgerResponseDto> updateLedger(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody LedgerRequestDto ledgerRequestDto) {
        return ApiResponse.success(SuccessCode.SUCCESS, ledgerService.updateLedger(userPrincipal.getUserId(), id, ledgerRequestDto));
    }

    @Operation(summary = "가계부 내역 삭제", description = "ID를 통해 특정 가계부 내역을 삭제합니다.")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ledgerService.isOwner(principal.userId, #id)")
    public ApiResponse<Void> deleteLedger(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        ledgerService.deleteLedger(userPrincipal.getUserId(), id);
        return ApiResponse.success(SuccessCode.DELETE_SUCCESS);
    }

    @Operation(summary = "영수증 사진 업로드", description = "영수증 이미지를 FastAPI 서버로 전송하여 분석을 요청합니다.")
    @PostMapping(value = "/receipt/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> uploadReceipt(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart("file") MultipartFile file) {
        ledgerService.uploadReceipt(userPrincipal.getUserId(), file);
        return ApiResponse.success(SuccessCode.SUCCESS);
    }

    @Operation(summary = "전국 평균 지출 비교", description = "나의 지출 총액과 전국 평균 지출을 비교합니다.")
    @GetMapping("/compare")
    public ApiResponse<LedgerComparisonResponseDto> compareLedger(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        LedgerComparisonResponseDto responseDto = ledgerService.compareWithNationalAverage(userPrincipal.getUserId());
        return ApiResponse.success(SuccessCode.SUCCESS, responseDto);
    }
}