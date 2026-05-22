package com.example.apiserver.domain.user.entity;

import com.example.apiserver.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("is_deleted = false")
public class User extends BaseTimeEntity {

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
    private boolean isDeleted = false;

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