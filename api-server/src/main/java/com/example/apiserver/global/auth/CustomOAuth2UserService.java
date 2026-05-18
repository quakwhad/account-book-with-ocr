package com.example.apiserver.global.auth;

import com.example.apiserver.domain.user.entity.Role;
import com.example.apiserver.domain.user.entity.User;
import com.example.apiserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
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
        
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String providerId = (String) attributes.get(userNameAttributeName);

        User user = saveOrUpdate(email, name, registrationId, providerId);

        return new DefaultOAuth2User(
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
