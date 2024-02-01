package com.appcenter.timepiece.dto.schedule;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleResponse {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ScheduleResponse(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
