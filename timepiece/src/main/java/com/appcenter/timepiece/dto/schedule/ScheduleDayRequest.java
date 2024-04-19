package com.appcenter.timepiece.dto.schedule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// todo: 중첩클래스로 작성
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleDayRequest {

    @NotNull @Size(min = 1)
    private List<ScheduleDto> schedule;

    public ScheduleDayRequest(List<ScheduleDto> schedule) {
        this.schedule = schedule;
    }
}
