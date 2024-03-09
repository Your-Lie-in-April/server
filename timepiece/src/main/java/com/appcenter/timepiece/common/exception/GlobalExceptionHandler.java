package com.appcenter.timepiece.common.exception;

import com.appcenter.timepiece.dto.CommonResponseDto;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    @ExceptionHandler(value = NotFoundMemberException.class)
    public ResponseEntity<CommonResponseDto> handleNotFoundMemberException(NotFoundMemberException ex){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonResponseDto<>(0, ex.getMessage(), null));
    }
    @ExceptionHandler(value = FailedCreateTokenException.class)
    public ResponseEntity<CommonResponseDto> handleTokenCreateError(FailedCreateTokenException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponseDto(0, ex.getMessage(), null));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<CommonResponseDto> handleSignatureException() {
        log.error("[SignatureException] 토큰에러");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CommonResponseDto(0, ExceptionMessage.TOKEN_INVALID_FORMAT.getMessage(), null));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<CommonResponseDto> handleMalformedJwtException() {
        log.error("[MalformedJwtException] 토큰에러");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CommonResponseDto(0, ExceptionMessage.TOKEN_INVALID_FORMAT.getMessage(), null));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CommonResponseDto> handleExpiredJwtException() {
        log.error("[ExpiredJwtException] 토큰에러");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CommonResponseDto(0, ExceptionMessage.TOKEN_EXPIRED.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponseDto> handleJwtEmptyException(){
        log.error("[JwtEmptyException] 토큰에러");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponseDto(0, ExceptionMessage.TOKEN_NOT_FOUND.getMessage(), null));
    }


}
