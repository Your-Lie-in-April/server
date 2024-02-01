package com.appcenter.timepiece.dto.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ProjectCreateUpdateRequest {

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
    private String color;
    private Long coverId;
}
