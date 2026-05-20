package com.example.apiserver.domain.ledger.service;

import com.example.apiserver.domain.ledger.dto.*;
import com.example.apiserver.domain.ledger.entity.Ledger;
import com.example.apiserver.domain.ledger.mapper.LedgerMapper;
import com.example.apiserver.domain.ledger.repository.LedgerRepository;
import com.example.apiserver.domain.user.entity.User;
import com.example.apiserver.domain.user.repository.UserRepository;
import com.example.apiserver.global.client.fastapi.FastApiClient;
import com.example.apiserver.global.client.fastapi.dto.ReceiptAnalysisResponseDto;
import com.example.apiserver.global.exception.CustomException;
import com.example.apiserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;
    private final FastApiClient fastApiClient;
    private final LedgerMapper ledgerMapper;

    @Value("${external-api.fastapi.callback-secret}")
    private String callbackSecret;

    public boolean isOwner(Long userId, Long ledgerId) {
        return ledgerRepository.existsByIdAndUserId(ledgerId, userId);
    }

    @Transactional
    public LedgerResponseDto createLedger(Long userId, LedgerRequestDto ledgerRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Ledger ledger = Ledger.builder()
                .user(user)
                .amount(ledgerRequestDto.amount())
                .category(ledgerRequestDto.category())
                .description(ledgerRequestDto.description())
                .type(ledgerRequestDto.type())
                .date(ledgerRequestDto.date())
                .build();
        return ledgerMapper.toDto(ledgerRepository.save(ledger));
    }

    public LedgerResponseDto getLedger(Long id) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LEDGER_NOT_FOUND));
        return ledgerMapper.toDto(ledger);
    }

    public Page<LedgerResponseDto> getMyLedgers(Long userId, Pageable pageable) {
        return ledgerRepository.findAllByUserId(userId, pageable).map(ledgerMapper::toDto);
    }

    public Page<LedgerResponseDto> getAllLedgersForAdmin(Pageable pageable) {
        return ledgerRepository.findAll(pageable).map(ledgerMapper::toDto);
    }

    public Page<LedgerResponseDto> getUserLedgersForAdmin(Long userId, Pageable pageable) {
        return ledgerRepository.findAllByUserId(userId, pageable).map(ledgerMapper::toDto);
    }

    @Transactional
    public LedgerResponseDto updateLedger(Long userId, Long id, LedgerRequestDto ledgerRequestDto) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LEDGER_NOT_FOUND));

        if (!ledger.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        ledger.update(
                ledgerRequestDto.amount(),
                ledgerRequestDto.category(),
                ledgerRequestDto.description(),
                ledgerRequestDto.type(),
                ledgerRequestDto.date()
        );

        return ledgerMapper.toDto(ledger);
    }

    @Transactional
    public void deleteLedger(Long userId, Long id) {
        Ledger ledger = ledgerRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LEDGER_NOT_FOUND));

        if (!ledger.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        ledgerRepository.delete(ledger);
    }

    public void uploadReceipt(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        fastApiClient.uploadReceipt(userId, file);
    }
}