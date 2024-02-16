package com.appcenter.timepiece.controller;


import com.appcenter.timepiece.common.security.Token;
import com.appcenter.timepiece.common.security.TokenService;
import com.appcenter.timepiece.dto.CommonResponseDto;
import com.appcenter.timepiece.dto.member.GoogleOAuthRequest;
import com.appcenter.timepiece.dto.member.GoogleOAuthResponse;
import com.appcenter.timepiece.service.OAuth2Service;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/v1/oauth2")
public class OAuth2Controller {

    OAuth2Service oAuth2Service;

    TokenService tokenService;

    @Autowired
    public OAuth2Controller(OAuth2Service oAuth2Service, TokenService tokenService){
        this.oAuth2Service = oAuth2Service;
        this.tokenService = tokenService;
    }

    @GetMapping(value = "/login/getGoogleAuthUrl")
    public ResponseEntity<?> getGoogleAuthUrl(HttpServletRequest request) throws Exception {

       return new ResponseEntity<>(oAuth2Service.makeLoginURI(), HttpStatus.MOVED_PERMANENTLY);

    }

    // 구글에서 리다이렉션
    @GetMapping(value = "/login/google")
    public ResponseEntity<CommonResponseDto> sign(HttpServletRequest request,
                                                                @RequestParam(value = "code") String authCode,
                                                                HttpServletResponse response) throws Exception{

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(1, " 성공", oAuth2Service.getGoogleInfo(authCode)));

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

            response.addHeader("Auth", newToken.getToken());
            response.addHeader("Refresh", newToken.getRefreshToken());
            response.setContentType("application/json;charset=UTF-8");

            return "HAPPY NEW TOKEN";
        }

        throw new RuntimeException();
    }
}
