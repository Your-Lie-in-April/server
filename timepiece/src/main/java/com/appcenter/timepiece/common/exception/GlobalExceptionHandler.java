package com.appcenter.timepiece.common.exception;

import com.appcenter.timepiece.common.dto.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NotFoundElementException.class)
    public ResponseEntity<CommonResponse> handleNotFoundElementException(NotFoundElementException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonResponse<>(0, ex.getMessage(), null));

    }

    @ExceptionHandler(value = FailedTokenCreateException.class)
    public ResponseEntity<CommonResponse> handleTokenCreateError(FailedTokenCreateException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponse(0, ex.getMessage(), null));
    }

    @ExceptionHandler(value = JwtEmptyException.class)
    public ResponseEntity<CommonResponse> handleJwtEmptyException(JwtEmptyException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new CommonResponse<>(0, ex.getMessage(), null));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CommonResponse> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("[ExpiredJwtException] 토큰에러");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(NotEnoughPrivilegeException.class)
    public ResponseEntity<CommonResponse<?>> handleNotEnoughPrivilegeException(NotEnoughPrivilegeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(e.getMessage(), null));
    }

    @ExceptionHandler(DeletedProjectException.class)
    public ResponseEntity<CommonResponse<?>> handleDeletedProjectException(DeletedProjectException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(e.getMessage(), null));
    }
}
