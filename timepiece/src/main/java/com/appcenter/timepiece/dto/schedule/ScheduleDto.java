package com.appcenter.timepiece.dto.schedule;

import com.appcenter.timepiece.domain.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleDto {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public ScheduleDto(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ScheduleDto from(Schedule schedule) {
        return new ScheduleDto(schedule.getStartTime(), schedule.getEndTime());
    }
}
