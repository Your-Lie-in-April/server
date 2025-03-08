package com.appcenter.timepiece.domain.project.dto;

import com.appcenter.timepiece.domain.project.entity.Project;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleWeekResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PinProjectResponse {

    private Long projectId;

    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Set<DayOfWeek> daysOfWeek;

    private String color;

    private String coverImageUrl;

    private Integer memberCount;

    private List<ScheduleWeekResponse> schedule;

    @Builder(access = AccessLevel.PUBLIC)
    private PinProjectResponse(Long projectId, String title, String description, LocalDate startDate, LocalDate endDate,
                               LocalTime startTime, LocalTime endTime,
                               Set<DayOfWeek> daysOfWeek,
                               String color, String coverImageUrl,
                               Integer memberCount, List<ScheduleWeekResponse> schedule) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.daysOfWeek = daysOfWeek;
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
                .daysOfWeek(project.getDaysOfWeek())
                .coverImageUrl(coverImageUrl)
                .color(project.getColor())
                .memberCount(schedule.size())
                .schedule(schedule)
                .build();
    }
}

