package com.appcenter.timepiece.dto.schedule;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleCreateUpdateRequest {

    private Long projectId;
    private List<ScheduleDayRequest> schedule;

}
