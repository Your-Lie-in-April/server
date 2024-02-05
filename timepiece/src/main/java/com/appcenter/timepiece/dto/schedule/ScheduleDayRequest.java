package com.appcenter.timepiece.dto.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 추후 중첩클래스로 수정?
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleDayRequest {
    private List<ScheduleResponse> schedule;

}
