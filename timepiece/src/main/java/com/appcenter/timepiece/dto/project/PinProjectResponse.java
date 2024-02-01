package com.appcenter.timepiece.dto.project;

import com.appcenter.timepiece.dto.schedule.ScheduleWeekResponse;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class PinProjectResponse {

    private Long projectId;
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
    private String coverImageUrl;
    private Integer memberCount;
    private List<ScheduleWeekResponse> schedule;


}

