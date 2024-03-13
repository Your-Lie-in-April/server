package com.appcenter.timepiece.common.exception;

public class JwtEmptyException extends RuntimeException{

    public JwtEmptyException(String m){
        super(m);
    }
}
