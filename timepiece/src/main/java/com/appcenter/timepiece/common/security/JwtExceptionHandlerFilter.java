package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.MismatchTokenTypeException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.EncodingException;
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

        try{
            filterChain.doFilter(request, response);
        }catch (MalformedJwtException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_UNAUTHENTICATED);
        }catch (ExpiredJwtException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_EXPIRED);
        }catch (IllegalArgumentException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_NOT_FOUND);
        }catch (SignatureException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
        }catch (StringIndexOutOfBoundsException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
        }catch (NullPointerException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_NOT_FOUND);
        }catch (MismatchTokenTypeException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
        }catch (EncodingException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
        }

    }
    private void setErrorResponse(
            HttpServletResponse response,
            ExceptionMessage exceptionMessage
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(exceptionMessage.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        CommonResponse commonResponse = new CommonResponse(exceptionMessage.getErrorCode(), exceptionMessage.getMessage(), null);
        try{
            response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}