package com.example.apiserver.domain.ledger.service;

import com.example.apiserver.domain.ledger.dto.*;
import com.example.apiserver.domain.ledger.entity.Ledger;
import com.example.apiserver.domain.ledger.entity.LedgerType; // LedgerType 임포트 추가
import com.example.apiserver.domain.ledger.mapper.LedgerMapper;
import com.example.apiserver.domain.ledger.repository.LedgerRepository;
import com.example.apiserver.domain.user.entity.User;
import com.example.apiserver.domain.user.repository.UserRepository;
import com.example.apiserver.global.client.fastapi.FastApiClient;
import com.example.apiserver.global.client.kosis.KosisApiClient;
import com.example.apiserver.global.client.kosis.dto.KosisStatisticResponseDto;
import com.example.apiserver.global.exception.CustomException;
import com.example.apiserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;
    private final FastApiClient fastApiClient;
    private final LedgerMapper ledgerMapper;
    private final KosisApiClient kosisApiClient;

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

        ledgerRepository.delete(ledger);
    }

    public void uploadReceipt(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        fastApiClient.uploadReceipt(userId, file);
    }

    public LedgerComparisonResponseDto compareWithNationalAverage(Long userId) {
        // 이번 달 기준 사용자의 실제 지출 합산
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        long myExpenditure = ledgerRepository.sumAmountByUserIdAndTypeAndDateBetween(
                userId, LedgerType.EXPENSE, startDate, endDate
        );

        // KOSIS 클라이언트를 통해 통계 데이터 조회 (12개 카테고리 리스트 반환)
        List<KosisStatisticResponseDto> averageDataList = kosisApiClient.getNationalAverageExpenditure();

        if (averageDataList.isEmpty()) {
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        }

        // 데이터 파싱 및 합산 (소수점 처리 포함)
        // 12개 카테고리의 모든 DT 값을 Double로 변환하여 합산
        double totalAverageDouble = averageDataList.stream()
                .mapToDouble(data -> Double.parseDouble(data.dt()))
                .sum();

        // 반올림하여 long 타입으로 변환 (총액)
        long nationalAverage = Math.round(totalAverageDouble);

        // 비교 로직 처리
        long difference = Math.abs(myExpenditure - nationalAverage);
        String message;

        if (myExpenditure > nationalAverage) {
            message = String.format("전국 1인가구 평균보다 %,d원 더 썼습니다.", difference);
        } else if (myExpenditure < nationalAverage) {
            message = String.format("전국 1인가구 평균보다 %,d원 덜 썼습니다. 훌륭합니다!", difference);
        } else {
            message = "전국 1인가구 평균 지출과 정확히 일치합니다.";
        }

        return LedgerComparisonResponseDto.builder()
                .myTotalExpenditure(myExpenditure)
                .nationalAverageExpenditure(nationalAverage)
                .differenceAmount(difference)
                .comparisonMessage(message)
                .build();
    }
}