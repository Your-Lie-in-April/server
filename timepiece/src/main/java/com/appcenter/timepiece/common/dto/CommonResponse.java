package com.appcenter.timepiece.common.dto;

import lombok.Getter;

@Getter
public class CommonResponse<T> {

    private String status;

    private String message;

    private T data;

    public CommonResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
