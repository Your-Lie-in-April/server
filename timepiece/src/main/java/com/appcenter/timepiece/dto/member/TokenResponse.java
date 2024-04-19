package com.appcenter.timepiece.dto.member;


import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenResponse {

    private String refreshToken;

    private String accessToken;

    @Builder
    private TokenResponse(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

}
