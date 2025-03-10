package com.appcenter.timepiece.domain.schedule.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ScheduleUtil {
    private static final int DAYS_IN_A_WEEK = 7;

    public static LocalDateTime calculateStartDay(LocalDate localDate) {
        LocalDateTime condition = LocalDateTime.of(localDate, LocalTime.MIN);
        return condition.minusDays(condition.getDayOfWeek().getValue() % DAYS_IN_A_WEEK);
    }

    public static LocalDateTime calculateStartDay(LocalDateTime localDateTime) {
        LocalDateTime condition = LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
        return condition.minusDays(condition.getDayOfWeek().getValue() % DAYS_IN_A_WEEK);
    }

    public static LocalDateTime calculateEndDay(LocalDateTime sundayOfWeek) {
        return sundayOfWeek.plusDays(DAYS_IN_A_WEEK);
    }
}
