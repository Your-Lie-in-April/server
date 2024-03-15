package com.appcenter.timepiece.common.exception;

public class NotFoundMemberException extends RuntimeException{
    public NotFoundMemberException(ExceptionMessage m){
        super(m.getMessage());
    }
}
