package com.example.apiserver.global.security.principal;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public record CustomOAuth2UserPrincipal(
        String email,
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> attributes,
        String nameAttributeKey
) implements OAuth2User {

    public String getEmail() {
        return email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return (String) attributes.get(nameAttributeKey);
    }
}