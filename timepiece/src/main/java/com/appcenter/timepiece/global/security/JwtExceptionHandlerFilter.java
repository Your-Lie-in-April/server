package com.appcenter.timepiece.global.security;

import com.appcenter.timepiece.global.common.dto.CommonResponse;
import com.appcenter.timepiece.global.exception.ExceptionMessage;
import com.appcenter.timepiece.global.exception.MismatchTokenTypeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (DecodingException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
            log.error("[JwtExceptionHandlerFilter] error name = DecodingException");
        } catch (MalformedJwtException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_UNAUTHENTICATED);
            log.error("[JwtExceptionHandlerFilter] error name = MalformedJwtException");
        } catch (ExpiredJwtException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_EXPIRED);
            log.error("[JwtExceptionHandlerFilter] error name = ExpiredJwtException");
        } catch (IllegalArgumentException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_NOT_FOUND);
            log.error("[JwtExceptionHandlerFilter] error name = IllegalArgumentException");
        } catch (SignatureException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
            log.error("[JwtExceptionHandlerFilter] error name = SignatureException");
        } catch (StringIndexOutOfBoundsException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
            log.error("[JwtExceptionHandlerFilter] error name = StringIndexOutOfBoundsException");
        } catch (NullPointerException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_NOT_FOUND);
            log.error("[JwtExceptionHandlerFilter] error name = NullPointerException");
        } catch (MismatchTokenTypeException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_TYPE_INVALID);
        }
    }

    private void setErrorResponse(
            HttpServletResponse response,
            ExceptionMessage exceptionMessage
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(exceptionMessage.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        CommonResponse commonResponse = new CommonResponse(exceptionMessage.getErrorCode(),
                exceptionMessage.getMessage(), null);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}