package com.appcenter.timepiece.dto.schedule;

import com.appcenter.timepiece.domain.Schedule;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleDto {

    @NotNull(message = "시작 날짜를 입력은 필수입니다.")
    private LocalDateTime startTime;

    @NotNull(message = "종료 날짜 입력은 필수입니다.")
    private LocalDateTime endTime;

    public ScheduleDto(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ScheduleDto from(Schedule schedule) {
        return new ScheduleDto(schedule.getStartTime(), schedule.getEndTime());
    }
}
