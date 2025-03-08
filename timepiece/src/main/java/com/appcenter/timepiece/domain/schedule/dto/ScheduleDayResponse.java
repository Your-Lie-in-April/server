package com.appcenter.timepiece.domain.schedule.dto;

import java.time.DayOfWeek;
import java.util.List;
import lombok.Getter;

@Getter
public class ScheduleDayResponse {

    private String daysOfWeek;

    private List<ScheduleDto> schedule;

    private ScheduleDayResponse(String daysOfWeek, List<ScheduleDto> schedule) {
        this.daysOfWeek = daysOfWeek;
        this.schedule = schedule;
    }

    public static ScheduleDayResponse of(DayOfWeek daysOfWeek, List<ScheduleDto> schedule) {
        return new ScheduleDayResponse(daysOfWeek.name(), schedule);
    }
}
