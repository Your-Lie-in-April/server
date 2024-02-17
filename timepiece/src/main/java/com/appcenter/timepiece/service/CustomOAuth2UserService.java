package com.appcenter.timepiece.service;

import com.appcenter.timepiece.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final RestTemplate restTemplate = new RestTemplate();

    private ObjectMapper objectMapper;

    private final MemberRepository memberRepository;
    private String googleAuthUrl = "https://oauth2.googleapis.com";

    private String googleLoginUrl = "https://accounts.google.com";

    private String googleClientId = "1049946425106-ksl6upcn28epp3vvdoop92hnjr9do226.apps.googleusercontent.com";

    private String googleRedirectUrl = "http://localhost:8080/v1/oauth2/login/google";

    private String googleClientSecret = "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";


    public HttpHeaders makeLoginURI(){
        String reqUrl = googleLoginUrl + "/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri=" + googleRedirectUrl
                + "&response_type=code&scope=email%20profile&access_type=offline";

        log.info("myLog-LoginUrl : {}",googleLoginUrl);
        log.info("myLog-ClientId : {}",googleClientId);
        log.info("myLog-RedirectUrl : {}",googleRedirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(reqUrl));

        return headers;
    }

    @Autowired
    public CustomOAuth2UserService( MemberRepository memberRepository, ObjectMapper objectMapper){
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
    }



    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("[OAuth2User] 시작");
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        log.info("[OAuth2User] 1");
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        log.info("[OAuth2User] 2");
        OAuth2Attribute oAuth2Attribute =
                OAuth2Attribute.ofGoogle(userNameAttributeName, oAuth2User.getAttributes());

        log.info("[CustomOAuth2UserService] oAuth2Attribute: {}", oAuth2Attribute);

        var memberAttribute = oAuth2Attribute.convertToMap();

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                memberAttribute, "email");
    }
}