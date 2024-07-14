package com.appcenter.timepiece.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommonCursorPagingResponse<T> {
    private Integer pageSize;
    private LocalDateTime nextCursor;
    private Boolean hasMore;
    private T data;

    public CommonCursorPagingResponse(Integer pageSize, LocalDateTime nextCursor, Boolean hasMore, T data) {
        this.pageSize = pageSize;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
        this.data = data;
    }
}
