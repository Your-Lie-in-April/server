package com.appcenter.timepiece.dto.project;

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

    private String coverImageUrl;

    private String color;

    @Builder(access = AccessLevel.PRIVATE)
    private ProjectResponse(Long projectId, String title, String description,
                            LocalDate startDate, LocalDate endDate,
                            LocalTime startTime, LocalTime endTime,
                            Set<DayOfWeek> daysOfWeek,
                            String coverImageUrl, String color) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = daysOfWeek;
        this.coverImageUrl = coverImageUrl;
        this.color = color;
    }

    public static ProjectResponse of(Project project, String coverImageUrl) {
        return ProjectResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .startTime(project.getStartTime())
                .endTime(project.getEndTime())
                .daysOfWeek(project.getDaysOfWeek())
                .coverImageUrl(coverImageUrl)
                .color(project.getColor())
                .build();
    }
}
