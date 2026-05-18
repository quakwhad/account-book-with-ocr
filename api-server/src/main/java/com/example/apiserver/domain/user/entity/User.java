package com.example.apiserver.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 시간 자동 기록을 위한 리스너 추가
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false, unique = true)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean isDeleted = false; // 1. 논리적 삭제 플래그 추가

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 가입일 추가

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정일 추가

    @Builder
    public User(String email, String name, String provider, String providerId, Role role) {
        this.email = email;
        this.name = name;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    public void update(String name) {
        this.name = name;
    }

    public void delete() {
        this.isDeleted = true;
        String suffix = "_deleted_" + UUID.randomUUID().toString().substring(0, 8);
        this.email = this.email + suffix;
        this.providerId = this.providerId + suffix;
    }
}