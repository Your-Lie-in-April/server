package com.appcenter.timepiece.common.exception;

public class NotEnoughPrivilegeException extends RuntimeException {
    public NotEnoughPrivilegeException(ExceptionMessage message) {
        super(message.getMessage());
    }
}
