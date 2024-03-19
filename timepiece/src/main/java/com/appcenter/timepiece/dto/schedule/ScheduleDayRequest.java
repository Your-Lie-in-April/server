package com.appcenter.timepiece.dto.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// todo: 중첩클래스로 작성
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleDayRequest {
    private List<ScheduleDto> schedule;

}
