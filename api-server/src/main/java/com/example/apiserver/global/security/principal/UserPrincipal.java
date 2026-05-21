package com.example.apiserver.global.security.principal;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class UserPrincipal extends User {
    private final Long userId;

    public UserPrincipal(Long userId, String email, Collection<? extends GrantedAuthority> authorities) {
        super(email, "", authorities);
        this.userId = userId;
    }
}
