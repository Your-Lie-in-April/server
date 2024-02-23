package com.appcenter.timepiece.common.exception;

import com.appcenter.timepiece.dto.CommonResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = TokenCreateException.class)
    public ResponseEntity<CommonResponseDto> handleTokenCreateError(TokenCreateException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonResponseDto(0, ex.getMessage(), null));
    }

    @ExceptionHandler(value = TokenExpiredException.class)
    public ResponseEntity<CommonResponseDto> handleTokenExpiredError(TokenExpiredException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonResponseDto(0, ex.getMessage(), null));
    }

}
