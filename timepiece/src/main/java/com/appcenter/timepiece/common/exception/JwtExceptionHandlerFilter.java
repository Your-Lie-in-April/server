package com.appcenter.timepiece.common.exception;

import ch.qos.logback.core.spi.ErrorCodes;
import com.appcenter.timepiece.dto.CommonResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            //토큰의 유효기간 만료
            setErrorResponse(response, ExceptionMessage.TOKEN_EXPIRED);
        } catch (IllegalArgumentException e) {
            //유효하지 않은 토큰
            setErrorResponse(response, ExceptionMessage.TOKEN_NOT_FOUND);
        } catch (SignatureException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
        } catch (MalformedJwtException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_UNAUTHENTICATED);
        }
    }

    private void setErrorResponse(
            HttpServletResponse response, ExceptionMessage exceptionMessage

    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(exceptionMessage.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        CommonResponseDto commonResponseDto = new CommonResponseDto(exceptionMessage.getErrorCode(), exceptionMessage.getMessage(), null);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(commonResponseDto));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}