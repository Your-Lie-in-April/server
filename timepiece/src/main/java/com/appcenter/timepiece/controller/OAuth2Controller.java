package com.appcenter.timepiece.controller;


import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.config.SwaggerApiResponses;
import com.appcenter.timepiece.service.OAuth2Service;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;


    @Operation(hidden = true)
    @GetMapping(value = "/login-page/google")
    public ResponseEntity<Void> getGoogleAuthUrl(HttpServletRequest request) throws Exception {

        return new ResponseEntity<>(oAuth2Service.makeLoginURI(), HttpStatus.MOVED_PERMANENTLY);

    }

//    @Operation(hidden = true)
//    @GetMapping(value = "/login/oauth2/code/{provider}")
//    public ResponseEntity<CommonResponse> sign(@RequestParam(value = "code") String authCode) throws Exception {
//
//        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "성공", null));
//
//    }

    @GetMapping(value = "/reissue")
    @Operation(summary = "토큰 재발급", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse> reissueAccessToken(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "토큰 재발급 성공", oAuth2Service.reissueAccessToken(request)));
    }

    @GetMapping(value = "/test")
    @Operation(summary = "테스트API", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse> testApi(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "테스트 성공", oAuth2Service.testApi(userDetails)));
    }

    @GetMapping(value = "/test1")
    @Operation(summary = "테스트API", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse> testApi1(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "테스트 성공", oAuth2Service.testApi(userDetails)));
    }
}
