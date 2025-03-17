package com.appcenter.timepiece.domain.schedule.service;

import static com.appcenter.timepiece.domain.schedule.util.ScheduleUtil.calculateStartDay;
import static com.appcenter.timepiece.domain.schedule.util.ScheduleUtil.extractFirstScheduleDate;

import com.appcenter.timepiece.domain.project.entity.Project;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleCreateUpdateRequest;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleDayRequest;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleDto;
import com.appcenter.timepiece.global.exception.ExceptionMessage;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduleCreateUpdateValidator {

    /**
     * ScheduleCreateUpdateRequest에 대한 모든 유효성 검사를 위임하는 메서드<br> Week, Day 범위로 검증을 위임한다.
     */
    public void validate(ScheduleCreateUpdateRequest req, Project project) {
        validateScheduleWeekRequest(req, project);
        for (ScheduleDayRequest scheduleDayRequest : req.getSchedule()) {
            validateDayAndLowLevelRequest(scheduleDayRequest, project);
        }
    }

    /**
     * // ScheduleCreateUpdateRequest Week 단위 검증 <br> 수행목록 <br> 1. validateIsIdenticalWeek - 일주일(일-토요일) 단위의 요청이 맞는지 검사
     * <br> // 2. validateIsIdenticalDayPerWeek - 중복된 날짜의 요청이 있는지 검사 <br> 3. validateIsAppropriatePeriodPerWeek - (생성 시
     * 정했던)프로젝트 기간 내인지 검사
     */
    private void validateScheduleWeekRequest(ScheduleCreateUpdateRequest req, Project project) {
        validateIsIdenticalWeek(req);
        validateIsIdenticalDayPerWeek(req);
        validateIsAppropriatePeriodPerWeek(req, project);
    }

    private void validateIsIdenticalWeek(ScheduleCreateUpdateRequest req) {
        LocalDate startDate = calculateStartDay(extractFirstScheduleDate(req)).toLocalDate();
        for (ScheduleDayRequest dayRequest : req.getSchedule()) {
            LocalDateTime validTarget = calculateStartDay(extractFirstScheduleDate(dayRequest));
            if (!Objects.equals(startDate, validTarget.toLocalDate())) {
                throw new IllegalArgumentException(ExceptionMessage.INVALID_WEEK.getMessage());
            }
        }
    }

    /**
     * 전날 Schedule이 다음날 00시에 종료하고, 다음날 Schedule이 존재한다면? -> StartTime으로만 확인한다.
     */
    private void validateIsIdenticalDayPerWeek(ScheduleCreateUpdateRequest req) {
        Set<LocalDate> uniqueDates = new HashSet<>();
        for (ScheduleDayRequest dayRequest : req.getSchedule()) {
            LocalDate firstScheduleDate = extractFirstScheduleDate(dayRequest).toLocalDate();
            if (uniqueDates.contains(firstScheduleDate)) {
                throw new IllegalArgumentException(ExceptionMessage.DUPLICATE_DATE.getMessage());
            }
            uniqueDates.add(firstScheduleDate);
        }
    }


    /**
     * 마지막날 24시 -> (마지막+1)일 00시는 허용토록 해야한다.
     */
    private void validateIsAppropriatePeriodPerWeek(ScheduleCreateUpdateRequest req, Project project) {
        // 요일 순서대로 정렬
        List<LocalDate> dates = req.getSchedule().stream()
                .map(dayRequest -> extractFirstScheduleDate(dayRequest).toLocalDate())
                .sorted()
                .toList();
        // 첫날과 마지막날만 프로젝트 기간 내인지 검사
        if (dates.get(0).isBefore(project.getStartDate()) || dates.get(dates.size() - 1)
                .isAfter(project.getEndDate())) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_PROJECT_PERIOD.getMessage());
        }
    }

    /**
     * ScheduleCreateUpdateRequest를 일(Day) 단위 및 ScheduleDto 단위 검증으로 위임한다.
     */
    private void validateDayAndLowLevelRequest(ScheduleDayRequest req, Project project) {
        validateScheduleDayRequest(req, project);
        for (ScheduleDto scheduleDto : req.getSchedule()) {
            validateScheduleDto(scheduleDto, project);
        }
    }

    /**
     * // ScheduleCreateUpdateRequest 일(Day) 단위 검증 <br> 수행목록 <br> 1. validateIsIdenticalDay - 모든 ScheduleDto의 동일한 날짜인지
     * // 검사<br> 2. validateDuplicateSchedulePerDay - ScheduleDto 간 요청 시간이 중복/교차되는지 검사<br> 3. //
     * validateIsAppropriateDayOfWeekPerDay - (생성 시 정했던)프로젝트 요일인지 검사
     */
    private void validateScheduleDayRequest(ScheduleDayRequest req, Project project) {
        validateIsIdenticalDay(req);
        validateDuplicateSchedulePerDay(req);
        validateIsAppropriateDayOfWeekPerDay(req, project);
    }

    private void validateIsIdenticalDay(ScheduleDayRequest req) {
        if (req.getSchedule().stream()
                .map(ScheduleDto::getStartTime)
                .map(LocalDateTime::toLocalDate).distinct().count() != 1L) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_DATE.getMessage());
        }
    }

    private void validateDuplicateSchedulePerDay(ScheduleDayRequest req) {
        req.getSchedule().sort(Comparator.comparing(ScheduleDto::getStartTime));
        LocalTime before = LocalTime.MIN;
        for (ScheduleDto scheduleDto : req.getSchedule()) {
            if (before.isAfter(scheduleDto.getStartTime().toLocalTime())) {
                throw new IllegalArgumentException(ExceptionMessage.INTERSECT_TIME.getMessage());
            }
            before = scheduleDto.getEndTime().toLocalTime();
        }
    }

    /**
     * startTime으로 비교 주의: 스케줄 생성 요청에 24시(00시)가 포함되는 경우, endTime에는 허용 요일의 다음날 00시가 포함될 수 있음
     */
    private void validateIsAppropriateDayOfWeekPerDay(ScheduleDayRequest req, Project project) {
        Set<DayOfWeek> dayOfWeeks = project.getDaysOfWeek();
        DayOfWeek day = extractFirstScheduleDate(req).getDayOfWeek();
        if (!dayOfWeeks.contains(day)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_PROJECT_DAY_OF_WEEK.getMessage());
        }
    }

    /**
     * // ScheduleCreateUpdateRequest ScheduleDto 단위 검증 <br> 수행목록 <br> 1. validateIsMultipleOfHalfHourPerSchedule - //
     * startTime, endTime이 30분 단위인지 검사 <br> 2. validateTimeSequencePerSchedule - startTime < endTime을 만족하는지 검사 <br> 3.
     * validateIsSameDayPerSchedule - startDate == endDate를 만족하는지 검사 <br> 4. validateIsAppropriateTimePerSchedule - (생성
     * 시 정했던)프로젝트 시간 내인지 검사
     */
    private void validateScheduleDto(ScheduleDto req, Project project) {
        LocalDateTime startDateTime = req.getStartTime();
        LocalDateTime endDateTime = req.getEndTime();

        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();

        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = endDateTime.toLocalTime();

        validateIsMultipleOfHalfHourPerSchedule(startTime, endTime);
        validateIsSameDayAndTimeSequencePerSchedule(startDate, startTime, endDate, endTime);
        validateIsAppropriateTimePerSchedule(startTime, endTime, project);
    }

    private void validateIsMultipleOfHalfHourPerSchedule(LocalTime startTime, LocalTime endTime) {
        if ((startTime.getMinute() % 30 != 0) || (endTime.getMinute() % 30 != 0)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_TIME_UNIT.getMessage());
        }
    }

    /**
     * 스케줄 생성 요청의 endTime이 자정일 경우, endDate가 startDate 보다 하루 이후여야 합니다. 자정이 아닐 경우, 동일 날짜이고 startTime < endTime을 만족해야 합니다.
     */
    private void validateIsSameDayAndTimeSequencePerSchedule(LocalDate startDate, LocalTime startTime,
                                                             LocalDate endDate, LocalTime endTime) {
        if (endTime.equals(LocalTime.MIN)) {
            if (startDate.plusDays(1).equals(endDate)) {
                return;
            }
            throw new IllegalArgumentException(ExceptionMessage.INVALID_TIME_SEQUENCE.getMessage());
        }
        if (Objects.equals(startDate, endDate)) {
            if (startTime.isAfter(endTime)) {
                throw new IllegalArgumentException(ExceptionMessage.INVALID_TIME_SEQUENCE.getMessage());
            }
            return;
        }
        throw new IllegalArgumentException(ExceptionMessage.IS_NOT_SAME_DAY.getMessage());
    }

    /**
     * 프로젝트 종료 시간이 00시인 경우 처리가 필요합니다.
     */
    private void validateIsAppropriateTimePerSchedule(LocalTime startTime, LocalTime endTime, Project project) {
        LocalTime lastTime = (project.getEndTime().equals(LocalTime.MIN)) ?
                LocalTime.of(23, 59, 59, 59) : project.getEndTime();
        if (startTime.isBefore(project.getStartTime()) || endTime.isAfter(lastTime)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_PROJECT_TIME.getMessage());
        }
    }
}
