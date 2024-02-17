package com.appcenter.timepiece.dto;

import lombok.Builder;

public class TokensDto {

    private String accessToken;
    private String refreshToken;

    @Builder
    public TokensDto(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
