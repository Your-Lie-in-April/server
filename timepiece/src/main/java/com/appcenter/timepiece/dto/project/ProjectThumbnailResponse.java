package com.appcenter.timepiece.dto.project;

import lombok.Getter;

@Getter
public class ProjectThumbnailResponse {

    private Long projectId;
    private String title;
    private String description;
    private String color;
    private String coverImageUrl;

    public ProjectThumbnailResponse(Long projectId, String title, String description,
                                    String color, String coverImageUrl) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.color = color;
        this.coverImageUrl = coverImageUrl;
    }
}
