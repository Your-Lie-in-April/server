package com.appcenter.timepiece.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;


@Component
@Slf4j
public class JwtAuthenticationEntryPointHandler implements AuthenticationEntryPoint {
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationEntryPointHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        log.info("JwtAuthenticationEntryPointHandler 접근");

        this.resolver = resolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        log.info("JwtAuthenticationEntryPointHandler commence 접근");
        resolver.resolveException(request, response, null, authException);
    }

}