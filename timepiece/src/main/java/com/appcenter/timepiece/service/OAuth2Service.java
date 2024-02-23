package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.redis.RefreshToken;
import com.appcenter.timepiece.common.redis.RefreshTokenRepository;
import com.appcenter.timepiece.common.security.JwtProvider;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.dto.member.GoogleOAuthRequest;
import com.appcenter.timepiece.dto.member.GoogleOAuthResponse;
import com.appcenter.timepiece.dto.member.OAuthMemberResponse;
import com.appcenter.timepiece.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
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

    private final JwtProvider jwtProvider;

    private ObjectMapper objectMapper;

    private final MemberRepository memberRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    private String googleAuthUrl = "https://oauth2.googleapis.com";

    private String googleLoginUrl = "https://accounts.google.com";

    private String googleClientId = "1049946425106-ksl6upcn28epp3vvdoop92hnjr9do226.apps.googleusercontent.com";

    private String googleRedirectUrl = "http://localhost:8080/v1/oauth2/login/google";

    private String googleClientSecret = "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";

    @Autowired
    public OAuth2Service(RefreshTokenRepository refreshTokenRepository, JwtProvider jwtProvider , Environment env, MemberRepository memberRepository, ObjectMapper objectMapper){
        this.env = env;
        this.memberRepository = memberRepository;
        this.objectMapper = objectMapper;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
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


        Optional<Member> member = memberRepository.findByEmail(oAuthMemberResponse.getEmail());
        List<String> role = new ArrayList<>();

        role.add("ROLE_USER");

        if(!(member.isPresent())) {

            Member registerMember = Member.builder()
                    .role(role)
                    .provider("Google")
                    .nickname(String.valueOf(oAuthMemberResponse.getGiven_name()))
                    .profileImageUrl(String.valueOf(oAuthMemberResponse.getPicture()))
                    .state("")
                    .email(String.valueOf(oAuthMemberResponse.getEmail()))
                    .build();

            memberRepository.save(registerMember);
            String accessToken = jwtProvider.createAccessToken(registerMember.getId(),registerMember.getEmail(), registerMember.getRole());
            String refreshToken = jwtProvider.createRefreshToken(registerMember.getId(),registerMember.getEmail(),registerMember.getRole());

            log.info("access: {}", accessToken);
            log.info("refresh: {}", refreshToken);

            //레디스에 Refresh 토큰을 저장한다. (사용자 기본키 Id, refresh 토큰, access 토큰 저장)
            refreshTokenRepository.save(new RefreshToken(registerMember.getId(), refreshToken));
        }


        return resultJson;

    }

    public String reissueAccessToken(HttpServletRequest request){

        String accessToken = "";
        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveRefreshToken(request));
        log.info("[reissueAccessToken] memberId 추출 성공. memberId = {}", memberId);
        Member member = memberRepository.findMemberById(memberId);
        log.info("[reissueAccessToken] member 찾기 성공. memberEmail = {}", member.getEmail());

        if((refreshTokenRepository.findByMemberId(memberId)).getRefreshToken().equals(jwtProvider.resolveRefreshToken(request))){
            accessToken = jwtProvider.createAccessToken(member.getId(), member.getEmail(), member.getRole());
            log.info("[reissueAccessToken] accessToken 생성 성공: {}", accessToken);
        }
        else{
            accessToken = null;
            new RuntimeException("accessToken 제작 실패");
        }
        return accessToken;
    }



}