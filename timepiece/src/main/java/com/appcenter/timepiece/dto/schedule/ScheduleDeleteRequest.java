package com.appcenter.timepiece.dto.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleDeleteRequest {

    private Long projectId;

    private LocalDate startDate;

    private LocalDate endDate;

}
