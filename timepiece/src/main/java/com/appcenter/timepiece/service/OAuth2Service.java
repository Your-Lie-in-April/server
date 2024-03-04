package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.FailedCreateTokenException;
import com.appcenter.timepiece.common.exception.NotFoundMemberException;
import com.appcenter.timepiece.common.redis.RefreshToken;
import com.appcenter.timepiece.common.redis.RefreshTokenRepository;
import com.appcenter.timepiece.common.security.JwtProvider;
import com.appcenter.timepiece.common.security.Role;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.dto.member.GoogleOAuthRequest;
import com.appcenter.timepiece.dto.member.GoogleOAuthResponse;
import com.appcenter.timepiece.dto.member.OAuthMemberResponse;
import com.appcenter.timepiece.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2Service {

    private final JwtProvider jwtProvider;

    private final ObjectMapper objectMapper;

    private final MemberRepository memberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private String googleAuthUrl = "https://oauth2.googleapis.com";

    private String googleLoginUrl = "https://accounts.google.com";

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUrl;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;


    public HttpHeaders makeLoginURI(){
        String reqUrl = googleLoginUrl + "/o/oauth2/v2/auth?client_id=" + googleClientId + "&redirect_uri=" + googleRedirectUrl
                + "&response_type=code&scope=email%20profile%20openid&access_type=offline";

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

        WebClient webClient = WebClient.create();

        GoogleOAuthResponse googleOAuthResponse = webClient.post()
                .uri(googleAuthUrl + "/token")
                .bodyValue(googleOAuthRequest)
                .retrieve()
                .bodyToMono(GoogleOAuthResponse.class)
                .block();

        log.info("responseBody {}", googleOAuthResponse);

        String googleToken = Objects.requireNonNull(googleOAuthResponse).getId_token();

        String requestUrl = UriComponentsBuilder.fromHttpUrl(googleAuthUrl + "/tokeninfo")
                .queryParam("id_token", googleToken)
                .toUriString();

        String resultJson = webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        OAuthMemberResponse oAuthMemberResponse = objectMapper.readValue(resultJson, OAuthMemberResponse.class);

        //로그인한 사람이 가입 이력이 있는지 DB 에서 찾아본다.
        Optional<Member> member = memberRepository.findByEmail(oAuthMemberResponse.getEmail());

        Member returnMember;
        Map<String, String> tokens = new HashMap<>();

        //만약 처음 로그인하는 사람이면 정보를 저장소에 저장해준다.
        if(member.isEmpty()) {
            log.info("[getGoogleInfo] 첫 로그인. 회원가입 시작");

            List<Role> role = new ArrayList<>();
            role.add(Role.ROLE_USER);

            returnMember = new Member("Google", oAuthMemberResponse.getGiven_name(),
                    oAuthMemberResponse.getEmail(), "", oAuthMemberResponse.getPicture(),role);

            memberRepository.save(returnMember);
            log.info("[getGoogleInfo] 회원가입 성공");
        }

        //만약 로그인 한 전적이 있는 사람은 DB 에서 사용자 정보를 가져온다.
        else{
            log.info("[getGoogleInfo] 이미 가입된 유저. 데이터베이스에서 사용자 정보 가져오기");
            returnMember = member.get();
            log.info("[getGoogleInfo] 로그인 성공");
        }

        //토큰 생성
        String accessToken = jwtProvider.createAccessToken(returnMember.getId(),returnMember.getEmail(), returnMember.getRole());
        String refreshToken = jwtProvider.createRefreshToken(returnMember.getId(),returnMember.getEmail(),returnMember.getRole());

        log.info("access: {}", accessToken);
        log.info("refresh: {}", refreshToken);
        tokens.put("Access", accessToken);
        tokens.put("Refresh", refreshToken);

        //레디스에 Refresh 토큰을 저장한다. (사용자 기본키 Id, refresh 토큰 저장)
        refreshTokenRepository.save(new RefreshToken(returnMember.getId(), refreshToken));

        return tokens;

    }

    //accessToken 재발급과 동시에 refreshToken 도 새로 발급한다.(유효시간을 늘리기 위함.)
    public Map<String, String> reissueAccessToken(HttpServletRequest request){

        Map<String, String> tokens = new HashMap<>();

        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveToken(request));
        log.info("[reissueAccessToken] memberId 추출 성공. memberId = {}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(ExceptionMessage.MEMBER_NOTFOUND));
        log.info("[reissueAccessToken] member 찾기 성공. memberEmail = {}", member.getEmail());

        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId);

        if(!(jwtProvider.validDateToken(jwtProvider.resolveToken(request)))){
            log.error("[reissueAccessToken] 토큰의 기한이 만료되었습니다. 재로그인 해주세요.");
            refreshTokenRepository.delete(refreshToken);
        }
        log.info("[reissueAccessToken] 이전 refreshToken: {}",refreshToken.getRefreshToken() );
        //refreshToken 의 유효 시간과, Header 에 담겨 온 RefreshToken 과 redis 에 저장되어있는 RefreshToken 과 일치하는지 비교한다.
        if(refreshToken.getRefreshToken().equals(jwtProvider.resolveToken(request))){

            String accessToken = jwtProvider.createAccessToken(memberId, member.getEmail(), member.getRole());
            log.info("[reissueAccessToken] accessToken 새로 발급 성공: {}", accessToken);

            String newRefreshToken = jwtProvider.createRefreshToken(memberId,member.getEmail(), member.getRole());
            log.info("[reissueAccessToken] refreshToken 새로 발급 성공: {}", newRefreshToken);

            tokens.put("Access", accessToken);
            tokens.put("Refresh", newRefreshToken);

            //redis 에 토큰 저장
            refreshTokenRepository.save(new RefreshToken(memberId, newRefreshToken));

            return tokens;
        }
        else {
            throw new FailedCreateTokenException("accessToken 제작 실패");
        }
    }



}