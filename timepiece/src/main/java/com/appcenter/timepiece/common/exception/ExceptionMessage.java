package com.appcenter.timepiece.common.exception;

public enum ExceptionMessage {
    TOKEN_EXPIRED("토큰이 만료되었습니다."),
    TOKEN_UNAUTHENTICATED("인증되지 않은 토큰입니다."),
    TOKEN_INVALID_FORMAT("잘못된 형식의 토큰입니다."),
    MEMBER_NOTFOUND("멤버가 존재하지 않습니다."),
    TOKEN_NOT_FOUND("토큰이 비었거나 null입니다")
    ;

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
