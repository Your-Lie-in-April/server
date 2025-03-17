package com.appcenter.timepiece.domain.schedule.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.appcenter.timepiece.domain.project.entity.Project;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleCreateUpdateRequest;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleDayRequest;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleDto;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("스케줄 생성 및 수정 커맨드 Validator 테스트")
class ScheduleCreateUpdateValidatorTest {

    private ScheduleCreateUpdateValidator validator = new ScheduleCreateUpdateValidator();


    private static ScheduleCreateUpdateRequest getScheduleCreateUpdateRequest() {
        // 2024.04.19 = 금요일
        ScheduleDto scheduleDto1 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 9, 30),
                LocalDateTime.of(2024, 4, 19, 10, 30));
        ScheduleDto scheduleDto2 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 8, 30),
                LocalDateTime.of(2024, 4, 19, 9, 0));
        ScheduleDto scheduleDto3 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 20, 19, 30),
                LocalDateTime.of(2024, 4, 20, 21, 30));
        ScheduleDto scheduleDto4 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 17, 9, 30),
                LocalDateTime.of(2024, 4, 17, 10, 30));
        ScheduleDto scheduleDto5 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 21, 9, 30),
                LocalDateTime.of(2024, 4, 21, 10, 30));

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(
                new ArrayList<>(List.of(scheduleDto1, scheduleDto2)));
        ScheduleDayRequest scheduleDayRequest2 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto3)));
        ScheduleDayRequest scheduleDayRequest3 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto4)));
        ScheduleDayRequest scheduleDayRequest4 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto5)));

        return new ScheduleCreateUpdateRequest(
                List.of(scheduleDayRequest1, scheduleDayRequest2, scheduleDayRequest3, scheduleDayRequest4));
    }

    @DisplayName("일주일 분량의 요청이 맞는지 검증한다.")
    @Test
    void validateIsIdenticalWeek() {
        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest = getScheduleCreateUpdateRequest();
        Project project = Project.builder()
                .title("테스트 프로젝트").
                description("테스트 프로젝트입니다.")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .coverId(null).color("FFFFFF")
                .build();

        assertThatThrownBy(() -> validator.validate(scheduleCreateUpdateRequest, project))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("일주일 범위를 초과");
    }


}