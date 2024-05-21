package com.appcenter.timepiece.controller;


import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.config.SwaggerApiResponses;
import com.appcenter.timepiece.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/v1/auth/reissue")
    @Operation(summary = "토큰 재발급", description = "")
    @SwaggerApiResponses
    public ResponseEntity<CommonResponse> reissueAccessToken(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new CommonResponse(1, "토큰 재발급 성공", authService.reissueAccessToken(request)));
    }

}
