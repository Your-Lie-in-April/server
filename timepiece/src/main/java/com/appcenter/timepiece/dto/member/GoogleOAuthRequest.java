package com.appcenter.timepiece.dto.member;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GoogleOAuthRequest {

    private String redirectUri;
    private String clientId;
    private String clientSecret;
    private String code;
    private String responseType;
    private String scope;
    private String accessType;
    private String grantType;
    private String state;
    private String includeGrantedScopes;
    private String loginHint;
    private String prompt;

}