package com.appcenter.timepiece.dto.project;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ProjectResponse {

    private String projectId;

    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean mon;

    private Boolean tue;

    private Boolean wed;

    private Boolean thu;

    private Boolean fri;

    private Boolean sat;

    private Boolean sun;

    private Boolean isStored;

    private String coverImageUrl;

    private String color;

    @Builder
    private ProjectResponse(String projectId, String title, String description,
                            LocalDate startDate, LocalDate endDate,
                            LocalTime startTime, LocalTime endTime,
                            Boolean mon, Boolean tue, Boolean wed, Boolean thu, Boolean fri,
                            Boolean sat, Boolean sun, Boolean isStored,
                            String coverImageUrl, String color) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mon = mon;
        this.tue = tue;
        this.wed = wed;
        this.thu = thu;
        this.fri = fri;
        this.sat = sat;
        this.sun = sun;
        this.isStored = isStored;
        this.coverImageUrl = coverImageUrl;
        this.color = color;
    }
}
