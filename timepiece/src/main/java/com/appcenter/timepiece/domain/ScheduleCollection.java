package com.appcenter.timepiece.domain;

import com.appcenter.timepiece.dto.schedule.ScheduleDayResponse;
import com.appcenter.timepiece.dto.schedule.ScheduleDto;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScheduleCollection {

    private final Map<DayOfWeek, List<Schedule>> schedulesByDayOfWeek;

    private ScheduleCollection(List<Schedule> schedules) {
        this.schedulesByDayOfWeek = groupingByDayOfWeek(schedules);
    }

    private Map<DayOfWeek, List<Schedule>> groupingByDayOfWeek(List<Schedule> schedules) {
        return schedules.stream().collect(Collectors.groupingBy(schedule -> schedule.getStartTime().getDayOfWeek()));
    }

    public static ScheduleCollection from(List<Schedule> schedules) {
        return new ScheduleCollection(schedules);
    }

    public List<ScheduleDayResponse> toScheduleDayResponses() {
        return schedulesByDayOfWeek.entrySet().stream()
                .map(e -> toScheduleDayResponse(e.getKey(), e.getValue()))
                .toList();
    }

    private ScheduleDayResponse toScheduleDayResponse(DayOfWeek dayOfWeek, List<Schedule> schedules) {
        return ScheduleDayResponse.of(dayOfWeek, toScheduleDtos(schedules));
    }

    private List<ScheduleDto> toScheduleDtos(List<Schedule> schedules) {
        return schedules.stream().map(ScheduleDto::from).toList();
    }
}
