package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.FailedTokenCreateException;
import com.appcenter.timepiece.common.exception.NotFoundElementException;
import com.appcenter.timepiece.common.redis.RefreshToken;
import com.appcenter.timepiece.common.redis.RefreshTokenRepository;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.common.security.JwtProvider;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2Service {

    private final JwtProvider jwtProvider;

    private final MemberRepository memberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    //accessToken 재발급과 동시에 refreshToken 도 새로 발급한다.(유효시간을 늘리기 위함.)
    public Map<String, String> reissueAccessToken(HttpServletRequest request) {

        Map<String, String> tokens = new HashMap<>();
        String token = jwtProvider.resolveServiceToken(request);

        Long memberId = jwtProvider.getMemberId(token);
        log.info("[reissueAccessToken] memberId 추출 성공. memberId = {}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        log.info("[reissueAccessToken] member 찾기 성공. memberEmail = {}", member.getEmail());

        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId);

        log.info("[reissueAccessToken] 이전 refreshToken: {}", refreshToken.getRefreshToken());
        //refreshToken 의 유효 시간과, Header 에 담겨 온 RefreshToken 과 redis 에 저장되어있는 RefreshToken 과 일치하는지 비교한다.
        if (refreshToken.getRefreshToken().equals(token)) {

            String accessToken = jwtProvider.createAccessToken(memberId, member.getEmail(), member.getRole());
            log.info("[reissueAccessToken] accessToken 새로 발급 성공: {}", accessToken);

            String newRefreshToken = jwtProvider.createRefreshToken(memberId, member.getEmail(), member.getRole());
            log.info("[reissueAccessToken] refreshToken 새로 발급 성공: {}", newRefreshToken);

            tokens.put("Access", accessToken);
            tokens.put("Refresh", newRefreshToken);

            //redis 에 토큰 저장
            refreshTokenRepository.save(new RefreshToken(memberId, newRefreshToken));

            return tokens;
        } else {
            throw new FailedTokenCreateException(ExceptionMessage.TOKEN_EXPIRED);
        }
    }

    public String testApi(UserDetails userDetails) {
        log.info("[testApi] memberId 추출중");

        Long memberId = ((CustomUserDetails) userDetails).getId();
        log.info("[testApi] memberId 추출 성공. memberId = {}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        log.info("[testApi] member 찾기 성공. memberEmail = {}", member.getEmail());

        return member.toString();
    }


}