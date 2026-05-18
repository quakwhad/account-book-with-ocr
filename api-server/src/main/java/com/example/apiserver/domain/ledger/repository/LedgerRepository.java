package com.example.apiserver.domain.ledger.repository;

import com.example.apiserver.domain.ledger.entity.Ledger;
import com.example.apiserver.domain.ledger.entity.LedgerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, Long> {

    Page<Ledger> findAllByUserId(Long userId, Pageable pageable);

    boolean existsByIdAndUserId(Long id, Long userId);

    // DB 단에서 합계만 계산하여 가져오도록 쿼리 추가
    @Query("SELECT COALESCE(SUM(l.amount), 0) FROM Ledger l WHERE l.user.id = :userId AND l.type = :type AND l.date >= :startDate AND l.date <= :endDate")
    Long sumAmountByUserIdAndTypeAndDateBetween(
            @Param("userId") Long userId,
            @Param("type") LedgerType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}