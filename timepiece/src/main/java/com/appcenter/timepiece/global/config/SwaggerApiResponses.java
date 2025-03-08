package com.appcenter.timepiece.global.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Operation()
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "201", description = "성공"),
        @ApiResponse(responseCode = "400", description =
                "예외 설명: <br><br>" +
                        "1. 토큰 타입이 틀렸습니다.<br><br>" +
                        "2. 멤버가 존재하지 않습니다."),
        @ApiResponse(responseCode = "401", description =
                "예외 설명: <br><br>" +
                        "1. 인증이 실패했습니다. => 토큰을 공백으로 보냈을 확률이 높습니다.<br><br>" +
                        "2. 토큰이 만료되었습니다.<br><br>3. 토큰이 비었거나 null입니다.<br><br>" +
                        "4. 잘못된 형식의 토큰입니다.<br><br>" +
                        "5. 인증되지 않은 토큰입니다."),
        @ApiResponse(responseCode = "403", description = "권한이 없습니다.")
})
public @interface SwaggerApiResponses {
}