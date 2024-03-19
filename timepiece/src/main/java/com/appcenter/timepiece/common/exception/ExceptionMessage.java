package com.appcenter.timepiece.common.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionMessage {
    TOKEN_EXPIRED("토큰이 만료되었습니다.", 0, HttpStatus.UNAUTHORIZED),
    TOKEN_UNAUTHENTICATED("인증되지 않은 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID_FORMAT("잘못된 형식의 토큰입니다.", 0, HttpStatus.UNAUTHORIZED),
    MEMBER_NOTFOUND("멤버가 존재하지 않습니다.", 0, HttpStatus.NOT_FOUND),
    TOKENTYPE_INVALID("토큰 타입이 틀렸습니다.", 0, HttpStatus.BAD_REQUEST),

    TOKEN_NOT_FOUND("토큰이 비었거나 null입니다", 0, HttpStatus.UNAUTHORIZED);


    private final String message;

    private final Integer errorCode;

    private final HttpStatus httpStatus;

    ExceptionMessage(String message, Integer errorCode, HttpStatus httpStatus) {
        this.message = message;
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
