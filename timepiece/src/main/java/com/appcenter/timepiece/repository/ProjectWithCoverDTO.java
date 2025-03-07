package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Project;
import lombok.Getter;

@Getter
public class ProjectWithCoverDTO {
    private final Project project;
    private final String thumbnailUrl;

    public ProjectWithCoverDTO(Project project, String thumbnailUrl) {
        this.project = project;
        this.thumbnailUrl = thumbnailUrl;
    }
}
