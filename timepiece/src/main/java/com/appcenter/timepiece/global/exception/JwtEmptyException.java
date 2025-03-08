package com.appcenter.timepiece.global.exception;

public class JwtEmptyException extends RuntimeException {

    public JwtEmptyException(String m) {
        super(m);
    }
}
