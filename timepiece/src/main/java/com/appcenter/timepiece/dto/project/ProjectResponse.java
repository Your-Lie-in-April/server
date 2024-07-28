package com.appcenter.timepiece.dto.project;

import com.appcenter.timepiece.domain.Cover;
import com.appcenter.timepiece.domain.Project;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
public class ProjectResponse {

    private Long projectId;

    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Set<DayOfWeek> daysOfWeek;

    private CoverInfo coverInfo;

    private String color;

    @Getter
    private static class CoverInfo {
        private Long id;
        private String thumbnailUrl;
        private String coverImageUrl;

        public CoverInfo(Long thumbnailId, String thumbnailUrl, String coverImageUrl) {
            this.id = thumbnailId;
            this.thumbnailUrl = thumbnailUrl;
            this.coverImageUrl = coverImageUrl;
        }

        public static CoverInfo of(Cover cover) {
            if (cover == null) return null;
            return new CoverInfo(cover.getId(), cover.getThumbnailUrl(), cover.getCoverImageUrl());
        }

    }

    @Builder(access = AccessLevel.PRIVATE)
    private ProjectResponse(Long projectId, String title, String description,
                            LocalDate startDate, LocalDate endDate,
                            LocalTime startTime, LocalTime endTime,
                            Set<DayOfWeek> daysOfWeek,
                            CoverInfo coverInfo, String color) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = daysOfWeek;
        this.coverInfo = coverInfo;
        this.color = color;
    }

    public static ProjectResponse of(Project project, Cover cover) {
        return ProjectResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .startTime(project.getStartTime())
                .endTime(project.getEndTime())
                .daysOfWeek(project.getDaysOfWeek())
                .coverInfo(CoverInfo.of(cover))
                .color(project.getColor())
                .build();
    }
}
