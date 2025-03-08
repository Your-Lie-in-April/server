package com.appcenter.timepiece.global.exception;

public class NotEnoughPrivilegeException extends RuntimeException {
    public NotEnoughPrivilegeException(ExceptionMessage message) {
        super(message.getMessage());
    }
}
