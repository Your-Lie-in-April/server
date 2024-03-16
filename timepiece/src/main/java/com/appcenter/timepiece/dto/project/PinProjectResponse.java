package com.appcenter.timepiece.dto.project;

import com.appcenter.timepiece.domain.Project;
import com.appcenter.timepiece.dto.schedule.ScheduleWeekResponse;
import lombok.AccessLevel;
import lombok.Builder;
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

    private String color;

    private String coverImageUrl;

    private Integer memberCount;

    private List<ScheduleWeekResponse> schedule;

    @Builder(access = AccessLevel.PRIVATE)
    private PinProjectResponse(Long projectId, String title, String description, LocalDate startDate, LocalDate endDate,
                               LocalTime startTime, LocalTime endTime,
                               Boolean mon, Boolean tue, Boolean wed, Boolean thu, Boolean fri, Boolean sat, Boolean sun,
                               String color, String coverImageUrl,
                               Integer memberCount, List<ScheduleWeekResponse> schedule) {
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
        this.color = color;
        this.coverImageUrl = coverImageUrl;
        this.memberCount = memberCount;
        this.schedule = schedule;
    }

    public static PinProjectResponse of(Project project, String coverImageUrl, List<ScheduleWeekResponse> schedule) {
        return PinProjectResponse.builder()
                .projectId(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .startTime(project.getStartTime())
                .endTime(project.getEndTime())
                .mon(project.getMon())
                .tue(project.getTue())
                .wed(project.getWed())
                .thu(project.getThu())
                .fri(project.getFri())
                .sat(project.getSat())
                .sun(project.getSun())
                .coverImageUrl(coverImageUrl)
                .color(project.getColor())
                .memberCount(schedule.size())
                .schedule(schedule)
                .build();
    }
}

