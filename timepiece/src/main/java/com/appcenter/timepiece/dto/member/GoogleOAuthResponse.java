package com.appcenter.timepiece.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GoogleOAuthResponse {

    private String access_token;
    private String expires_in;
    private String refreshToken;
    private String scope;
    private String token_type;
    private String id_token;

}