package com.appcenter.timepiece.dto.schedule;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ScheduleDeleteRequest {

    private Long projectId;
    private LocalDate startDate;
    private LocalDate endDate;

}
