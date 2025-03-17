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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("스케줄 생성 및 수정 커맨드 Validator 테스트")
class ScheduleCreateUpdateValidatorTest {

    private ScheduleCreateUpdateValidator validator = new ScheduleCreateUpdateValidator();

    @DisplayName("주 단위 검증")
    @Nested
    class WeekTest {

        Project project;

        @BeforeEach
        void setUp() {
            project = Project.builder()
                    .title("테스트 프로젝트").
                    description("테스트 프로젝트입니다.")
                    .startDate(LocalDate.MIN).endDate(LocalDate.MAX)
                    .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                    .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                    .coverId(null).color("FFFFFF")
                    .build();
        }

        @DisplayName("일주일 분량의 요청이 맞는지 검증한다.")
        @Test
        void validateIsIdenticalWeek() {
            // 2024.04.19 = 금요일
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 9, 30),
                    LocalDateTime.of(2024, 4, 19, 10, 30));
            // 2024.04.21 = 일요일(다음주)
            ScheduleDto scheduleDto2 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 21, 9, 30),
                    LocalDateTime.of(2024, 4, 21, 10, 30));
            ScheduleDayRequest dayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
            ScheduleDayRequest dayRequest2 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto2)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest1, dayRequest2));

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("일주일 범위를 초과");
        }

        @DisplayName("ScheduleDayRequest 간 중복된 요일이 없는지 검증한다.")
        @Test
        void validateIsIdenticalDayPerWeek() {
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 9, 30),
                    LocalDateTime.of(2024, 4, 19, 10, 30));
            ScheduleDto scheduleDto2 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 10, 30),
                    LocalDateTime.of(2024, 4, 19, 12, 30));
            ScheduleDayRequest dayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
            ScheduleDayRequest dayRequest2 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto2)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest1, dayRequest2));

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("중복된 날짜");
        }

        @DisplayName("날짜가 'startDate <= [TARGET] <= endDate' 이내인지 검증한다.")
        @Test
        void validateIsInProjectPeriod() {
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2025, 4, 16, 19, 30),
                    LocalDateTime.of(2025, 4, 16, 20, 30));
            ScheduleDayRequest dayRequest = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest));
            Project project = Project.builder()
                    .title("테스트 프로젝트").
                    description("테스트 프로젝트입니다.")
                    .startDate(LocalDate.of(2020, 1, 1))
                    .endDate(LocalDate.of(2020, 1, 2))
                    .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                    .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                    .coverId(null).color("FFFFFF")
                    .build();

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("프로젝트 수행 기간을 벗어납니다");
        }
    }

    @DisplayName("일 단위 검증")
    @Nested
    class DayTest {

        Project project;

        @BeforeEach
        void setUp() {
            project = Project.builder()
                    .title("테스트 프로젝트").
                    description("테스트 프로젝트입니다.")
                    .startDate(LocalDate.MIN).endDate(LocalDate.MAX)
                    .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                    .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                    .coverId(null).color("FFFFFF")
                    .build();
        }

        @DisplayName("모든 ScheduleDto 날짜가 동일한지 검증한다.")
        @Test
        void validateIsIdenticalDay() {
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 9, 30),
                    LocalDateTime.of(2024, 4, 19, 10, 30));
            ScheduleDto scheduleDto2 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 18, 8, 30),
                    LocalDateTime.of(2024, 4, 18, 9, 0));
            ScheduleDayRequest dayRequest = new ScheduleDayRequest(
                    new ArrayList<>(List.of(scheduleDto1, scheduleDto2)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest));

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("같은 날짜여야 합니다");
        }
    }
}