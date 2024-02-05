package com.appcenter.timepiece.dto.schedule;

import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleDayResponse {

    private String daysOfWeek;

    private List<ScheduleResponse> schedule;

    public ScheduleDayResponse(String daysOfWeek, List<ScheduleResponse> schedule) {
        this.daysOfWeek = daysOfWeek;
        this.schedule = schedule;
    }
}
