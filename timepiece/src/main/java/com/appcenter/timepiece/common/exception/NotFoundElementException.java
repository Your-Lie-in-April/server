package com.appcenter.timepiece.common.exception;

public class NotFoundElementException extends RuntimeException {
    public NotFoundElementException(ExceptionMessage m) {
        super(m.getMessage());
    }
}
