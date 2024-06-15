package com.appcenter.timepiece.dto.cover;

import com.appcenter.timepiece.domain.Cover;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoverDataResponse {
    String id;
    String url;

    private CoverDataResponse(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public static CoverDataResponse of(Cover cover) {
        return new CoverDataResponse(cover.getId().toString(), cover.getCoverImageUrl());
    }
}
