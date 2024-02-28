package com.appcenter.timepiece.controller;


import com.appcenter.timepiece.dto.CommonResponseDto;
import com.appcenter.timepiece.service.OAuth2Service;
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


    @GetMapping(value = "/login/getGoogleAuthUrl")
    public ResponseEntity<Void> getGoogleAuthUrl(HttpServletRequest request) throws Exception {

        return new ResponseEntity<>(oAuth2Service.makeLoginURI(), HttpStatus.MOVED_PERMANENTLY);

    }

    // 구글에서 리다이렉션
    @GetMapping(value = "/login/google")
    public ResponseEntity<CommonResponseDto> sign(HttpServletRequest request,
                                                  @RequestParam(value = "code") String authCode,
                                                  HttpServletResponse response) throws Exception{

        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(1, " 성공", oAuth2Service.getGoogleInfo(authCode)));

    }

    @PostMapping(value = "/reissue")
    public ResponseEntity<CommonResponseDto> reissueAccessToken(HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(1, " 성공", oAuth2Service.reissueAccessToken(request)));
    }

    @GetMapping(value = "/test")
    public ResponseEntity<CommonResponseDto> testApi(){
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponseDto(1, "테스트 성공", null));
    }
}
