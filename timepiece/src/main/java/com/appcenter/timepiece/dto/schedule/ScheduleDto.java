package com.appcenter.timepiece.dto.schedule;

import com.appcenter.timepiece.domain.Schedule;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleDto {

    @NotNull(message = "프로젝트 시작 날짜를 입력 해 주세요.")
    private LocalDateTime startTime;

    @NotNull(message = "프로젝트 마지막 날짜를 입력 해 주세요.")
    private LocalDateTime endTime;

    public ScheduleDto(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ScheduleDto from(Schedule schedule) {
        return new ScheduleDto(schedule.getStartTime(), schedule.getEndTime());
    }
}
