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

    private String coverImageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private ProjectThumbnailResponse(Long projectId, String title, String description,
                                    String color, String coverImageUrl) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.color = color;
        this.coverImageUrl = coverImageUrl;
    }

    public static ProjectThumbnailResponse of(Project project, String coverImageUrl) {
        return ProjectThumbnailResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .color(project.getColor())
                .coverImageUrl(coverImageUrl)
                .build();
    }
}
