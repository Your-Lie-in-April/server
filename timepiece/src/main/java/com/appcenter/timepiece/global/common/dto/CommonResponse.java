package com.appcenter.timepiece.global.common.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonResponse<T> {

    private int status;

    private String message;

    private T data;

    public CommonResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<T>(HttpStatus.OK.value(), message, data);
    }

    public static <T> CommonResponse<T> error(String message, T data) {
        return new CommonResponse<T>(HttpStatus.BAD_REQUEST.value(), message, data);
    }

}
