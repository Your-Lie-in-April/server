package com.appcenter.timepiece.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {


    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("[doFilerInternal] 토큰 얻어오기");
        String token = jwtProvider.resolveToken(servletRequest);
        log.info("[doFilterInternal] 토큰 얻어오기 성공");

        log.info("[doFilterInternal] Token = {}", token);

        if (token != null) {
            filterChain.doFilter(servletRequest, servletResponse);
            try{
                jwtProvider.validDateToken(token);
                log.info("결과: {}, ", jwtProvider.validDateToken(token));
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("[doFilterInternal] 토큰 값 검증 완료");

            }
            catch (Exception e){
                servletRequest.setAttribute("exception", e);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}