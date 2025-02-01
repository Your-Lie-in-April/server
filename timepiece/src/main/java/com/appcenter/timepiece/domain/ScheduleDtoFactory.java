package com.appcenter.timepiece.domain;

import com.appcenter.timepiece.dto.schedule.ScheduleDayResponse;
import com.appcenter.timepiece.dto.schedule.ScheduleDto;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class ScheduleDtoFactory {
    public static List<ScheduleDto> scheduleDtosFrom(List<Schedule> schedules) {
        return schedules.stream().map(ScheduleDto::from).toList();
    }

    public static List<ScheduleDayResponse> scheduleDayResponsesFrom(
            Map<DayOfWeek, List<Schedule>> scheduleDtoByDayOfWeek) {
        return scheduleDtoByDayOfWeek.entrySet()
                .stream()
                .map(e -> ScheduleDayResponse.of(e.getKey(),
                        ScheduleDtoFactory.scheduleDtosFrom(e.getValue())))
                .toList();

    }
}
