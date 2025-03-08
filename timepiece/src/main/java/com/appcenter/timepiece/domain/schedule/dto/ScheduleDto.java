package com.appcenter.timepiece.domain.schedule.dto;

import com.appcenter.timepiece.domain.schedule.entity.Schedule;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;

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
