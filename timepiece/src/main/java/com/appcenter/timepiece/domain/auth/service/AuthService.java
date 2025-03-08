package com.appcenter.timepiece.domain.auth.service;

import com.appcenter.timepiece.domain.auth.dto.TokenResponse;
import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.domain.member.repository.MemberRepository;
import com.appcenter.timepiece.global.exception.ExceptionMessage;
import com.appcenter.timepiece.global.exception.FailedTokenCreateException;
import com.appcenter.timepiece.global.exception.NotFoundElementException;
import com.appcenter.timepiece.global.redis.RefreshToken;
import com.appcenter.timepiece.global.redis.RefreshTokenRepository;
import com.appcenter.timepiece.global.security.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;

    private final MemberRepository memberRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    //accessToken 재발급과 동시에 refreshToken 도 새로 발급한다.(유효시간을 늘리기 위함.)
    public TokenResponse reissueAccessToken(HttpServletRequest request) {

        String token = jwtProvider.resolveServiceToken(request);

        Long memberId = jwtProvider.getMemberId(token);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        RefreshToken refreshToken = refreshTokenRepository.findByMemberId(memberId);

        //refreshToken 의 유효 시간과, Header 에 담겨 온 RefreshToken 과 redis 에 저장되어있는 RefreshToken 과 일치하는지 비교한다.

        if (!refreshToken.getRefreshToken().equals(token)) {
            throw new FailedTokenCreateException(ExceptionMessage.TOKEN_EXPIRED);
        }

        String accessToken = jwtProvider.createAccessToken(memberId, member.getEmail(), member.getRole());

        String newRefreshToken = jwtProvider.createRefreshToken(memberId, member.getEmail(), member.getRole());

        TokenResponse tokenResponse = TokenResponse.builder()
                .refreshToken(newRefreshToken)
                .accessToken(accessToken)
                .build();

        //redis 에 토큰 저장
        refreshTokenRepository.save(new RefreshToken(memberId, newRefreshToken));

        return tokenResponse;
    }


}