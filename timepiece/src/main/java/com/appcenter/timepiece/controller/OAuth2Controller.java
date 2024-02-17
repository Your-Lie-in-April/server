package com.appcenter.timepiece.controller;


import com.appcenter.timepiece.common.security.Token;
import com.appcenter.timepiece.common.security.TokenService;
import com.appcenter.timepiece.dto.CommonResponseDto;
import com.appcenter.timepiece.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/oauth2")
public class OAuth2Controller {

    CustomOAuth2UserService customOAuth2UserService;

    TokenService tokenService;

    @Autowired
    public OAuth2Controller(CustomOAuth2UserService customOAuth2UserService, TokenService tokenService){
        this.customOAuth2UserService = customOAuth2UserService;
        this.tokenService = tokenService;
    }

    @GetMapping(value = "/login/getGoogleAuthUrl")
    public ResponseEntity<?> getGoogleAuthUrl(HttpServletRequest request) throws Exception {

       return new ResponseEntity<>(customOAuth2UserService.makeLoginURI(), HttpStatus.MOVED_PERMANENTLY);

    }

    // 구글에서 리다이렉션
    @GetMapping(value = "/login/google")
    public String sign(@RequestParam String code){
        return "구글 로그인 리다이렉트, code: " + code;
    }

    @GetMapping("/token/expired")
    public String auth() {
        throw new RuntimeException();
    }

    @GetMapping("/token/refresh")
    public String refreshAuth(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader("Refresh");

        if (token != null && tokenService.verifyToken(token)) {
            String email = tokenService.getUid(token);
            Token newToken = tokenService.generateToken(email, "USER");

            response.addHeader("Auth", newToken.getAccessToken());
            response.addHeader("Refresh", newToken.getRefreshToken());
            response.setContentType("application/json;charset=UTF-8");

            return null;
        }

        throw new RuntimeException();
    }
}
