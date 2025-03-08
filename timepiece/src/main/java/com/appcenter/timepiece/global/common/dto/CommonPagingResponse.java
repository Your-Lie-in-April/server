package com.appcenter.timepiece.global.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonPagingResponse<T> {
    private Integer currentPage;
    private Integer pageSize;
    private Long totalCount;
    private Integer totalPages;
    private T data;

    public CommonPagingResponse(Integer currentPage, Integer pageSize, Long totalCount, Integer totalPages, T data) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPages = totalPages;
        this.data = data;
    }
}
