package com.appcenter.timepiece.global.exception;

public class FailedTokenCreateException extends RuntimeException {

    public FailedTokenCreateException(ExceptionMessage message) {
        super(message.getMessage());
    }
}
