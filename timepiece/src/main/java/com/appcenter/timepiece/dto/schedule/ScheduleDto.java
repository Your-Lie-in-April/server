package com.appcenter.timepiece.dto.schedule;

import com.appcenter.timepiece.domain.Schedule;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleDto {

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    public ScheduleDto(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ScheduleDto from(Schedule schedule) {
        return new ScheduleDto(schedule.getStartTime(), schedule.getEndTime());
    }
}
