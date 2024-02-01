package com.appcenter.timepiece.dto.schedule;

import lombok.Getter;

import java.util.List;

// 추후 중첩클래스로 수정?
@Getter
public class ScheduleDayRequest {
    private List<ScheduleResponse> schedule;

}
