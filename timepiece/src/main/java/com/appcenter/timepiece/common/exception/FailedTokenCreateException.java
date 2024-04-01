package com.appcenter.timepiece.common.exception;

public class FailedTokenCreateException extends RuntimeException {

    public FailedTokenCreateException(ExceptionMessage message) {
        super(message.getMessage());
    }
}
