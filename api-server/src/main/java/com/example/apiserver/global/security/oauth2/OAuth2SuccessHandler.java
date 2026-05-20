package com.example.apiserver.global.security.oauth2;

import com.example.apiserver.domain.user.entity.User;
import com.example.apiserver.domain.user.repository.UserRepository;
import com.example.apiserver.global.exception.CustomException;
import com.example.apiserver.global.exception.ErrorCode;
import com.example.apiserver.global.security.jwt.JwtTokenProvider;
import com.example.apiserver.global.security.oauth2.principal.CustomOAuth2UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.success-redirect-url}")
    private String successRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // CustomOAuth2UserPrincipal 캐스팅을 통해 프로바이더 독립적으로 이메일 추출 가능
        CustomOAuth2UserPrincipal oAuth2User = (CustomOAuth2UserPrincipal) authentication.getPrincipal();
        String email = oAuth2User.getEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole().getKey());

        String targetUrl = UriComponentsBuilder.fromUriString(successRedirectUrl)
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}