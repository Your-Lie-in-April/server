package com.appcenter.timepiece.dto.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleCreateUpdateRequest {

    private Long projectId;
    private List<ScheduleDayRequest> schedule;

}
