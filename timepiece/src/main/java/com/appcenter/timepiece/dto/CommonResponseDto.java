package com.appcenter.timepiece.dto;

import lombok.Getter;

@Getter
public class CommonResponseDto<T> {


    private final int code;


    private final String message;


    private final T data;

    public CommonResponseDto(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

}
