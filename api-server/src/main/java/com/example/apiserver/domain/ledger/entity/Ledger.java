package com.example.apiserver.domain.ledger.entity;

import com.example.apiserver.domain.user.entity.User;
import com.example.apiserver.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "ledgers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ledger extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String category;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LedgerType type;

    @Column(nullable = false)
    private LocalDate date;

    @Builder
    public Ledger(User user, Long amount, String category, String description, LedgerType type, LocalDate date) {
        this.user = user;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.type = type;
        this.date = date;
    }

    public void update(Long amount, String category, String description, LedgerType type, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.type = type;
        this.date = date;
    }
}