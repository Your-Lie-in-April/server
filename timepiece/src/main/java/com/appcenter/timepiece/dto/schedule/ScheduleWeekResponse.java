package com.appcenter.timepiece.dto.schedule;

import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleWeekResponse {
    private String nickname;
    private List<ScheduleDayResponse> schedule;

    public ScheduleWeekResponse(String nickname, List<ScheduleDayResponse> schedule) {
        this.nickname = nickname;
        this.schedule = schedule;
    }
}
