package com.appcenter.timepiece.global.exception;

public class NotFoundElementException extends RuntimeException {
    public NotFoundElementException(ExceptionMessage m) {
        super(m.getMessage());
    }
}
