package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.common.exception.TokenExpiredException;
import com.appcenter.timepiece.common.redis.RefreshTokenRepository;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {


    private final JwtProvider jwtProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("[doFilerInternal] access 토큰 얻어오기");
        String accessToken = jwtProvider.resolveAccessToken(servletRequest);
        log.info("[doFilterInternal] access 토큰 얻어오기 성공");

        log.info("[doFilterInternal] accessToken = {}", accessToken);

        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            Authentication authentication = jwtProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("[doFilterInternal] 토큰 값 검증 완료");
        }
        else{
            new TokenExpiredException("[doFilterInternal] 토큰의 기한이 만료되었습니다.");
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}