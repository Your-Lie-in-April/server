package com.appcenter.timepiece.dto.project;

import com.appcenter.timepiece.domain.Project;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProjectThumbnailResponse {

    private Long projectId;

    private String title;

    private String description;

    private String color;

    private String thumbnailUrl;

    @Builder(access = AccessLevel.PUBLIC)
    private ProjectThumbnailResponse(Long projectId, String title, String description,
                                     String color, String thumbnailUrl) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.color = color;
        this.thumbnailUrl = thumbnailUrl;
    }

    public ProjectThumbnailResponse(Project project, String thumbnailUrl) {
        this.projectId = project.getId();
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.color = project.getColor();
        this.thumbnailUrl = thumbnailUrl;
    }

    public static ProjectThumbnailResponse of(Project project, String thumbnailUrl) {
        return ProjectThumbnailResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .color(project.getColor())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}
