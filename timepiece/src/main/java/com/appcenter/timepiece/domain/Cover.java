package com.appcenter.timepiece.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cover {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "thumbnail_url", columnDefinition = "TEXT")
    private String thumbnailUrl;

    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;

    @OneToMany(mappedBy = "cover")
    private List<Project> projects = new ArrayList<>();

    private Cover(String thumbnailUrl, String coverImageUrl) {
        this.thumbnailUrl = thumbnailUrl;
        this.coverImageUrl = coverImageUrl;
    }

    public static Cover of(String thumbnailUrl, String coverImageUrl) {
        return new Cover(thumbnailUrl, coverImageUrl);
    }
}
