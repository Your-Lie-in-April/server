package com.appcenter.timepiece.global.exception;

import com.appcenter.timepiece.global.common.dto.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NotFoundElementException.class)
    public ResponseEntity<CommonResponse> handleNotFoundElementException(NotFoundElementException ex) {
        log.error("[handleNotFoundElementException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonResponse.error(ex.getMessage(), null));

    }

    @ExceptionHandler(value = FailedTokenCreateException.class)
    public ResponseEntity<CommonResponse> handleTokenCreateError(FailedTokenCreateException ex) {
        log.error("[handleTokenCreateError] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(value = JwtEmptyException.class)
    public ResponseEntity<CommonResponse> handleJwtEmptyException(JwtEmptyException ex) {
        log.error("[handleJwtEmptyException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CommonResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        log.error("[handleExpiredJwtException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(NotEnoughPrivilegeException.class)
    public ResponseEntity<CommonResponse<?>> handleNotEnoughPrivilegeException(NotEnoughPrivilegeException ex) {
        log.error("[handleNotEnoughPrivilegeException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalStateException(IllegalStateException ex) {
        log.error("[handleNotEnoughPrivilegeException {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("[handleIllegalArgumentException] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(DeletedProjectException.class)
    public ResponseEntity<CommonResponse<?>> handleDeletedProjectException(DeletedProjectException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonResponse.error(e.getMessage(), null));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("유효성 검사 실패: {}", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(ExceptionMessage.FAILED_VALIDATION.getMessage(), errorMessage));
    }
}
