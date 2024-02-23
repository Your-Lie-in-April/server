package com.appcenter.timepiece.common.exception;

public class TokenExpiredException extends RuntimeException{

    public TokenExpiredException(String m){
        super(m);
    }
}
