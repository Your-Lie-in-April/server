package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.dto.TokenDto;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
public class MemberRequestMapper {
    public TokenDto toDto(OAuth2User oAuth2User) {
        var attributes = oAuth2User.getAttributes();
        return TokenDto.builder()
                .email((String)attributes.get("email"))
                .name((String)attributes.get("name"))
                .picture((String)attributes.get("picture"))
                .build();
    }
//
//    public UserFindRequest toFindDto(TokenDto tokenDto) {
//        return new UserFindRequest(tokenDto.getEmail());
//    }
}