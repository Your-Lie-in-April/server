package com.appcenter.timepiece.domain;

import com.appcenter.timepiece.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cover extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;

    private Cover(String thumbnailUrl, String coverImageUrl) {
        this.thumbnailUrl = thumbnailUrl;
        this.coverImageUrl = coverImageUrl;
    }

    public static Cover of(String thumbnailUrl, String coverImageUrl) {
        return new Cover(thumbnailUrl, coverImageUrl);
    }
}
