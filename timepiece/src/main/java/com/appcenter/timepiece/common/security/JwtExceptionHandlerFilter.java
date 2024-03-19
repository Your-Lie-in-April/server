package com.appcenter.timepiece.common.security;

import com.appcenter.timepiece.common.dto.CommonResponse;
import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.MismatchTokenTypeException;

<<<<<<< HEAD:timepiece/src/main/java/com/appcenter/timepiece/common/security/JwtExceptionHandlerFilter.java
=======
import com.appcenter.timepiece.common.dto.CommonResponse;
>>>>>>> main:timepiece/src/main/java/com/appcenter/timepiece/common/exception/JwtExceptionHandlerFilter.java
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
<<<<<<< HEAD:timepiece/src/main/java/com/appcenter/timepiece/common/security/JwtExceptionHandlerFilter.java
        }catch (DecodingException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
            log.info("[JwtExceptionHandlerFilter] error name = DecodingException");
        }catch (MalformedJwtException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_UNAUTHENTICATED);
            log.info("[JwtExceptionHandlerFilter] error name = MalformedJwtException");
        }catch (ExpiredJwtException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_EXPIRED);
            log.info("[JwtExceptionHandlerFilter] error name = ExpiredJwtException");
        }catch (IllegalArgumentException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_NOT_FOUND);
            log.info("[JwtExceptionHandlerFilter] error name = IllegalArgumentException");
        }catch (SignatureException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
            log.info("[JwtExceptionHandlerFilter] error name = SignatureException");
        }catch (StringIndexOutOfBoundsException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
            log.info("[JwtExceptionHandlerFilter] error name = StringIndexOutOfBoundsException");
        }catch (NullPointerException e){
            setErrorResponse(response, ExceptionMessage.TOKEN_NOT_FOUND);
            log.info("[JwtExceptionHandlerFilter] error name = NullPointerException");
        }catch (MismatchTokenTypeException e){
            setErrorResponse(response, ExceptionMessage.TOKENTYPE_INVALID);
            log.info("[JwtExceptionHandlerFilter] error name = MismatchTokenTypeException");
=======
        } catch (MalformedJwtException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_UNAUTHENTICATED);
        } catch (ExpiredJwtException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_EXPIRED);
        } catch (IllegalArgumentException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_NOT_FOUND);
        } catch (SignatureException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
        } catch (StringIndexOutOfBoundsException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_INVALID_FORMAT);
        } catch (NullPointerException e) {
            setErrorResponse(response, ExceptionMessage.TOKEN_NOT_FOUND);
        } catch (Exception e) {
            setErrorResponse(response, ExceptionMessage.MEMBER_NOTFOUND);
>>>>>>> main:timepiece/src/main/java/com/appcenter/timepiece/common/exception/JwtExceptionHandlerFilter.java
        }

    }

    private void setErrorResponse(
            HttpServletResponse response,
            ExceptionMessage exceptionMessage
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(exceptionMessage.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
<<<<<<< HEAD:timepiece/src/main/java/com/appcenter/timepiece/common/security/JwtExceptionHandlerFilter.java
        CommonResponse commonResponse = new CommonResponse(exceptionMessage.getErrorCode(), exceptionMessage.getMessage(), null);
        try{
            response.getWriter().write(objectMapper.writeValueAsString(commonResponse));
        }catch (IOException e){
=======
        CommonResponse commonResponseDto = new CommonResponse(exceptionMessage.getErrorCode(), exceptionMessage.getMessage(), null);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(commonResponseDto));
        } catch (IOException e) {
>>>>>>> main:timepiece/src/main/java/com/appcenter/timepiece/common/exception/JwtExceptionHandlerFilter.java
            e.printStackTrace();
        }
    }
}