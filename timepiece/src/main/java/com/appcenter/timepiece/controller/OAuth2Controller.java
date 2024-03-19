package com.appcenter.timepiece.controller;


import com.appcenter.timepiece.common.dto.CommonResponse;

import com.appcenter.timepiece.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;


    @GetMapping(value = "/login-page/google")
    public ResponseEntity<Void> getGoogleAuthUrl(HttpServletRequest request) throws Exception {

        return new ResponseEntity<>(oAuth2Service.makeLoginURI(), HttpStatus.MOVED_PERMANENTLY);

    }

    @Operation(hidden = true)
    @GetMapping(value = "/login/google")
    public ResponseEntity<CommonResponse> sign(HttpServletRequest request,
                                               @RequestParam(value = "code") String authCode,
                                               HttpServletResponse response) throws Exception {

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", oAuth2Service.getGoogleInfo(authCode)));

    }

    @GetMapping(value = "/reissue")
    public ResponseEntity<CommonResponse> reissueAccessToken(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "토큰 재발급 성공", oAuth2Service.reissueAccessToken(request)));
    }

    @GetMapping(value = "/test")
    public ResponseEntity<CommonResponse> testApi(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "테스트 성공", oAuth2Service.testApi(request)));
    }

    @GetMapping(value = "/test1")
    public ResponseEntity<CommonResponse> testApi1(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "테스트 성공", oAuth2Service.testApi(request)));
    }
}
