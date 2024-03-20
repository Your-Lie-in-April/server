package com.appcenter.timepiece.dto.schedule;

import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleDayResponse {

    private String daysOfWeek;

    private List<ScheduleDto> schedule;

    public ScheduleDayResponse(String daysOfWeek, List<ScheduleDto> schedule) {
        this.daysOfWeek = daysOfWeek;
        this.schedule = schedule;
    }
}
