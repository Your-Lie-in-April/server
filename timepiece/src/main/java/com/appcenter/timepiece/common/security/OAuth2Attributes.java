package com.appcenter.timepiece.common.security;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@ToString
@Slf4j
public class OAuth2Attributes {
    private Map<String, Object> attributes;     // OAuth2 반환하는 유저 정보
    private String nameAttributesKey;
    private String name;
    private String email;
    private String profileImageUrl;
    private String provider;
    private String oauth2Id;

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributesKey,
                            String name, String email, String profileImageUrl, String provider, String oauth2Id) {
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
        this.oauth2Id = oauth2Id;
    }

    public static OAuth2Attributes of(String socialName, Map<String, Object> attributes) {
        if ("kakao".equals(socialName)) {
            return ofKakao("id", attributes);
        } else if ("google".equals(socialName)) {
            return ofGoogle("sub", attributes);
        }
        return ofGithub("id", attributes);
    }


    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .name(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .profileImageUrl(String.valueOf(attributes.get("picture")))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .provider("google")
                .oauth2Id(String.valueOf(attributes.get(userNameAttributeName)))
                .build();
    }

    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Attributes.builder()
                .name(String.valueOf(kakaoProfile.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .profileImageUrl(String.valueOf(kakaoProfile.get("profile_image_url")))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .provider("kakao")
                .oauth2Id(String.valueOf(attributes.get(userNameAttributeName)))
                .build();
    }

    private static OAuth2Attributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .name(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("login")))
                .profileImageUrl(String.valueOf(attributes.get("avatar_url")))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .provider("github")
                .oauth2Id(String.valueOf(attributes.get(userNameAttributeName)))
                .build();
    }
}
