package com.appcenter.timepiece.support.auth.controller;

import com.appcenter.timepiece.global.security.JwtProvider;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev")
@Profile("dev") // 개발 환경에서만 활성화
@RequiredArgsConstructor
public class TestTokenController {

    private final JwtProvider jwtProvider; // 실제 토큰 발급 로직을 담고 있는 서비스

    @GetMapping("/token")
    public Map<String, String> getTestToken(
            @RequestParam(defaultValue = "1") Long id,
            @RequestParam(defaultValue = "user1@example.com") String email,
            @RequestParam(defaultValue = "ROLE_USER") String role) {

        String token = jwtProvider.createAccessToken(id, email, List.of(role));
        return Collections.singletonMap("token", token);
    }
}