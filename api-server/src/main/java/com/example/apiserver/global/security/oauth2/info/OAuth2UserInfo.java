package com.example.apiserver.global.security.oauth2.info;

public interface OAuth2UserInfo {
    String getProviderId(); // 소셜 서비스의 고유 식별자
    String getProvider();   // "google", "naver", "kakao" 등
    String getEmail();
    String getName();
}