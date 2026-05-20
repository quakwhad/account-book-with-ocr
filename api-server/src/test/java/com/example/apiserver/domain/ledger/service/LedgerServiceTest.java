package com.example.apiserver.domain.ledger.service;

import com.example.apiserver.domain.ledger.dto.LedgerRequestDto;
import com.example.apiserver.domain.ledger.dto.LedgerResponseDto;
import com.example.apiserver.domain.ledger.entity.Ledger;
import com.example.apiserver.domain.ledger.entity.LedgerType;
import com.example.apiserver.domain.ledger.mapper.LedgerMapper;
import com.example.apiserver.domain.ledger.repository.LedgerRepository;
import com.example.apiserver.domain.user.entity.Role;
import com.example.apiserver.domain.user.entity.User;
import com.example.apiserver.domain.user.repository.UserRepository;
import com.example.apiserver.global.client.fastapi.FastApiClient;
import com.example.apiserver.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @Mock
    private LedgerRepository ledgerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LedgerMapper ledgerMapper;

    @Mock
    private FastApiClient fastApiClient;

    @InjectMocks
    private LedgerService ledgerService;

    @Test
    @DisplayName("가계부 내역 생성 성공")
    void createLedger_Success() {
        // given
        Long userId = 1L;
        User user = User.builder().email("test@example.com").name("Tester").role(Role.USER).provider("google").providerId("123").build();
        ReflectionTestUtils.setField(user, "id", userId);
        
        LedgerRequestDto requestDto = new LedgerRequestDto(10000L, "식비", "점심", LedgerType.EXPENSE, LocalDate.now());
        
        Ledger ledger = Ledger.builder()
                .user(user)
                .amount(requestDto.amount())
                .category(requestDto.category())
                .description(requestDto.description())
                .type(requestDto.type())
                .date(requestDto.date())
                .build();
        ReflectionTestUtils.setField(ledger, "id", 1L);

        LedgerResponseDto responseDto = new LedgerResponseDto(1L, userId, 10000L, "식비", "점심", LedgerType.EXPENSE, LocalDate.now());

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(ledgerRepository.save(any(Ledger.class))).willReturn(ledger);
        given(ledgerMapper.toDto(any(Ledger.class))).willReturn(responseDto);

        // when
        LedgerResponseDto result = ledgerService.createLedger(userId, requestDto);

        // then
        assertThat(result.amount()).isEqualTo(requestDto.amount());
        verify(ledgerRepository).save(any(Ledger.class));
    }

    @Test
    @DisplayName("가계부 내역 생성 실패 - 사용자 없음")
    void createLedger_UserNotFound() {
        // given
        Long userId = 1L;
        LedgerRequestDto requestDto = new LedgerRequestDto(10000L, "식비", "점심", LedgerType.EXPENSE, LocalDate.now());
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> ledgerService.createLedger(userId, requestDto))
                .isInstanceOf(CustomException.class);
    }
}
