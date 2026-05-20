package com.example.apiserver.domain.auth.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class AuthController {

    @GetMapping("/token")
    public String showToken(@RequestParam String token) {
        return "로그인 성공! 아래 토큰을 복사하여 Swagger의 Authorize 버튼에 입력하세요.\n\n" + token;
    }
}
