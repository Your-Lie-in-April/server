package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.TokenCreateException;
import com.appcenter.timepiece.common.exception.TokenExpiredException;
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
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

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

    public Map<String, String> getGoogleInfo(String authCode) throws JsonProcessingException {

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

        //로그인한 사람이 가입 이력이 있는지 DB 에서 찾아본다.
        Optional<Member> member = memberRepository.findByEmail(oAuthMemberResponse.getEmail());
        List<String> role = new ArrayList<>();

        role.add("ROLE_USER");
        Map<String, String> tokens = new HashMap<>();

        //만약 처음 로그인하는 사람이면 정보를 저장소에 저장해준다.
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
            tokens.put("Access", accessToken);
            tokens.put("Refresh", refreshToken);

            //레디스에 Refresh 토큰을 저장한다. (사용자 기본키 Id, refresh 토큰 저장)
            refreshTokenRepository.save(new RefreshToken(registerMember.getId(), refreshToken));
        }

        //만약 로그인 한 전적이 있는 사람은 DB 에서 사용자 정보를 가져온다.
        else{
            Member serviceMember = memberRepository.getByEmail(String.valueOf(oAuthMemberResponse.getEmail()));
            String accessToken = jwtProvider.createAccessToken(serviceMember.getId(),serviceMember.getEmail(), serviceMember.getRole());
            String refreshToken = jwtProvider.createRefreshToken(serviceMember.getId(),serviceMember.getEmail(),serviceMember.getRole());

            log.info("access: {}", accessToken);
            log.info("refresh: {}", refreshToken);
            tokens.put("Access", accessToken);
            tokens.put("Refresh", refreshToken);

            //레디스에 Refresh 토큰을 저장한다. (사용자 기본키 Id, refresh 토큰 저장)
            refreshTokenRepository.save(new RefreshToken(serviceMember.getId(), refreshToken));

        }

        return tokens;

    }

    //accessToken 재발급과 동시에 refreshToken 도 새로 발급한다.(유효시간을 늘리기 위함.)
    public Map<String, String> reissueAccessToken(HttpServletRequest request){

        Map<String, String> tokens = new HashMap<>();

        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveRefreshToken(request));
        log.info("[reissueAccessToken] memberId 추출 성공. memberId = {}", memberId);
        Member member = memberRepository.findMemberById(memberId);
        log.info("[reissueAccessToken] member 찾기 성공. memberEmail = {}", member.getEmail());

        final RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId);

        if(!(jwtProvider.validateToken(jwtProvider.resolveRefreshToken(request)))){
            log.error("[reissueAccessToken] 토큰의 기한이 만료되었습니다. 재로그인 해주세요.");
            refreshTokenRepository.delete(refreshToken);
            new TokenExpiredException("토큰의 기한이 만료되었습니다. 재로그인 해주세요.");
        }
        //refreshToken 의 유효 시간과, Header 에 담겨 온 RefreshToken 과 redis 에 저장되어있는 RefreshToken 과 일치하는지 비교한다.
        if(refreshToken.getRefreshToken().equals(jwtProvider.resolveRefreshToken(request))){
            String accessToken = jwtProvider.createAccessToken(memberId, member.getEmail(), member.getRole());
            log.info("[reissueAccessToken] accessToken 새로 발급 성공: {}", accessToken);
            String newRefreshToken = jwtProvider.createRefreshToken(memberId,member.getEmail(), member.getRole());
            log.info("[reissueAccessToken] refreshToken 새로 발급 성공: {}", newRefreshToken);

            tokens.put("Access", accessToken);
            tokens.put("Refresh", newRefreshToken);

            //redis 에 토큰 저장
            refreshTokenRepository.save(new RefreshToken(memberId, newRefreshToken));

        }
        else{
            new TokenCreateException("accessToken 제작 실패");
        }
        return tokens;
    }



}