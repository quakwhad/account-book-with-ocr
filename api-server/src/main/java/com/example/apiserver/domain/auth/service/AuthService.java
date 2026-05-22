package com.example.apiserver.domain.auth.service;

import com.example.apiserver.domain.auth.dto.TokenResponseDto;
import com.example.apiserver.domain.auth.entity.RefreshToken;
import com.example.apiserver.domain.auth.repository.RefreshTokenRepository;
import com.example.apiserver.domain.user.entity.User;
import com.example.apiserver.domain.user.repository.UserRepository;
import com.example.apiserver.global.exception.CustomException;
import com.example.apiserver.global.exception.ErrorCode;
import com.example.apiserver.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public TokenResponseDto refreshToken(String refreshToken, HttpServletResponse response) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_TOKEN));

        User user = savedToken.getUser();

        // 새 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole().getKey());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole().getKey());

        // DB 업데이트
        savedToken.updateToken(newRefreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(14 * 24 * 60 * 60)
                .sameSite("Lax")
                // .secure(true) // HTTPS 적용 시 활성화
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // Access Token만 JSON으로 반환
        return TokenResponseDto.of(newAccessToken, null);
    }

    @Transactional
    public void saveRefreshToken(User user, String refreshToken) {
        refreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .user(user)
                                .token(refreshToken)
                                .build())
                );
    }
}
