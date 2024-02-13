package com.appcenter.timepiece.service;

import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.dto.member.GoogleOAuthRequest;
import com.appcenter.timepiece.dto.member.GoogleOAuthResponse;
import com.appcenter.timepiece.dto.member.OAuthMemberResponse;
import com.appcenter.timepiece.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OAuth2Service {

    private final Environment env;
    private final RestTemplate restTemplate = new RestTemplate();

    private ObjectMapper objectMapper;

    private final MemberRepository memberRepository;
    private String googleAuthUrl = "https://oauth2.googleapis.com";

    private String googleLoginUrl = "https://accounts.google.com";

    private String googleClientId = "1049946425106-ksl6upcn28epp3vvdoop92hnjr9do226.apps.googleusercontent.com";

    private String googleRedirectUrl = "http://localhost:8080/v1/oauth2/login/google";

    private String googleClientSecret = "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";

    @Autowired
    public OAuth2Service(Environment env, MemberRepository memberRepository, ObjectMapper objectMapper){
        this.env = env;
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
    }

    public HttpHeaders makeLoginURI(){
        String reqUrl = googleLoginUrl + "/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri=" + googleRedirectUrl
                + "&response_type=code&scope=email%20profile%20openid&access_type=offline";

        log.info("myLog-LoginUrl : {}",googleLoginUrl);
        log.info("myLog-ClientId : {}",googleClientId);
        log.info("myLog-RedirectUrl : {}",googleRedirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(reqUrl));

        return headers;
    }

    public String getGoogleInfo(String authCode) throws JsonProcessingException {

        GoogleOAuthRequest googleOAuthRequest = GoogleOAuthRequest
                .builder()
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .code(authCode)
                .redirectUri(googleRedirectUrl)
                .grantType("authorization_code")
                .build();

        RestTemplate restTemplate = new RestTemplate();


        ResponseEntity<GoogleOAuthResponse> apiResponse = restTemplate.postForEntity(googleAuthUrl + "/token", googleOAuthRequest, GoogleOAuthResponse.class);

        GoogleOAuthResponse googleOAuthResponse = apiResponse.getBody();

        log.info("responseBody {}",googleOAuthResponse.toString());


        String googleToken = googleOAuthResponse.getId_token();


        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleAuthUrl + "/tokeninfo").queryParam("id_token",googleToken).toUriString();


        String resultJson = restTemplate.getForObject(requestUrl, String.class);

        OAuthMemberResponse oAuthMemberResponse = objectMapper.readValue(resultJson, OAuthMemberResponse.class);

        log.info("image = {}", oAuthMemberResponse.getName());

        Optional<Member> member = memberRepository.findByEmail(oAuthMemberResponse.getEmail());
        List<String> role = new ArrayList<>();

        role.add("ROLE_USER");

        if(!(member.isPresent())){

            Member registerMember = Member.builder()
                    .role(role)
                    .provider("Google")
                    .nickname(String.valueOf(oAuthMemberResponse.getGiven_name()))
                    .profileImageUrl(String.valueOf(oAuthMemberResponse.getPicture()))
                    .state("")
                    .email(String.valueOf(oAuthMemberResponse.getEmail()))
                    .build();

            memberRepository.save(registerMember);
        }


        return resultJson;


    }



}
