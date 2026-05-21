package com.example.apiserver.global.security.service;

import com.example.apiserver.domain.user.entity.Role;
import com.example.apiserver.domain.user.entity.User;
import com.example.apiserver.domain.user.repository.UserRepository;
import com.example.apiserver.global.security.principal.CustomOAuth2UserPrincipal;
import com.example.apiserver.global.security.oauth2.info.GoogleOAuth2UserInfo;
import com.example.apiserver.global.security.oauth2.info.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Value("${app.admin-email:root@example.com}")
    private String adminEmail;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 프로바이더별 인터페이스 분기 조립 (나중에 카카오, 네이버가 생기면 여기 else if만 추가됨)
        OAuth2UserInfo oAuth2UserInfo;
        if (registrationId.equalsIgnoreCase("google")) {
            oAuth2UserInfo = new GoogleOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 제공자입니다: " + registrationId);
        }

        User user = saveOrUpdate(
                oAuth2UserInfo.getEmail(),
                oAuth2UserInfo.getName(),
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getProviderId()
        );

        // 성공 핸들러로 안전하게 넘겨주기 위해 CustomOAuth2UserPrincipal 반환
        return new CustomOAuth2UserPrincipal(
                user.getEmail(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                attributes,
                userNameAttributeName
        );
    }

    private User saveOrUpdate(String email, String name, String provider, String providerId) {
        Role role = email.equals(adminEmail) ? Role.ROOT : Role.USER;

        User user = userRepository.findByEmail(email)
                .map(entity -> {
                    entity.update(name);
                    return entity;
                })
                .orElseGet(() -> User.builder()
                        .email(email)
                        .name(name)
                        .provider(provider)
                        .providerId(providerId)
                        .role(role)
                        .build());

        return userRepository.save(user);
    }
}