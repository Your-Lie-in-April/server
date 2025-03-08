package com.appcenter.timepiece.domain.schedule.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class ScheduleWeekResponse {
    private String nickname;
    private List<ScheduleDayResponse> schedule;

    public ScheduleWeekResponse(String nickname, List<ScheduleDayResponse> schedule) {
        this.nickname = nickname;
        this.schedule = schedule;
    }
}
