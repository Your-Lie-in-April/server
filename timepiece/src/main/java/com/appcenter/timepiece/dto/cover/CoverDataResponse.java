package com.appcenter.timepiece.dto.cover;

import com.appcenter.timepiece.domain.Cover;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoverDataResponse {

    private Long id;
    private String thumbnailUrl;
    private String coverImageUrl;

    private CoverDataResponse(Long id, String thumbnailUrl, String coverImageUrl) {
        this.id = id;
        this.thumbnailUrl = thumbnailUrl;
        this.coverImageUrl = coverImageUrl;
    }

    public static CoverDataResponse of(Cover cover) {
        return new CoverDataResponse(cover.getId(), cover.getThumbnailUrl(), cover.getCoverImageUrl());
    }
}
