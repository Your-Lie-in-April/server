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
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("스케줄 생성 및 수정 커맨드 Validator 테스트")
class ScheduleValidatorTest {

    private ScheduleValidator validator = new ScheduleValidator();

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

        @DisplayName("ScheduleDto 간 시간 중복/교차가 없는지 검증한다.")
        @Test
        void validateDuplicateSchedulePerDay() {
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 10, 30),
                    LocalDateTime.of(2024, 4, 19, 20, 0));
            ScheduleDto scheduleDto2 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 19, 30),
                    LocalDateTime.of(2024, 4, 19, 20, 30));

            ScheduleDayRequest dayRequest = new ScheduleDayRequest(
                    new ArrayList<>(List.of(scheduleDto1, scheduleDto2)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest));

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("중복/교차되는 시간");
        }

        @DisplayName("ScheduleDayRequest가 프로젝트 수행일아 아닌 경우 예외를 발생한다")
        @Test
        void validateIsAppropriateDayOfWeekPerDay() {
            // 2024.04.19 = 금요일
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 10, 30),
                    LocalDateTime.of(2024, 4, 19, 20, 0));
            ScheduleDayRequest dayRequest = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest));
            Project project = Project.builder()
                    .title("테스트 프로젝트").
                    description("테스트 프로젝트입니다.")
                    .startDate(LocalDate.MIN).endDate(LocalDate.MAX)
                    .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                    .daysOfWeek(new HashSet<>(Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)))
                    .coverId(null).color("FFFFFF")
                    .build();

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("프로젝트 수행일이 아님");
        }
    }

    @DisplayName("ScheduleDto 단위 검증")
    @Nested
    class ScheduleDtoTest {

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

        @DisplayName("시작/종료 시간이 30분 단위가 아닐 경우 예외를 발생한다")
        @Test
        void validateIsHalfUnits() {
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 9, 20),
                    LocalDateTime.of(2024, 4, 19, 10, 30));
            ScheduleDayRequest dayRequest = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest));

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("30분 단위의 시간이어야 합니다");
        }

        @DisplayName("시작 시간이 종료 시간 보다 뒤면 예외를 발생한다")
        @Test
        void validateTimeSequence() {
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 23, 30),
                    LocalDateTime.of(2024, 4, 19, 9, 30));
            ScheduleDayRequest dayRequest = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest));

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("startTime이 endTime 이후일 수 없습니다");
        }

        @DisplayName("시작 날짜와 종료 날짜가 다르면 예외를 발생한다")
        @Test
        void validateIsSameDayShouldThrow() {
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 10, 30),
                    LocalDateTime.of(2024, 4, 20, 11, 30));
            ScheduleDayRequest dayRequest = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest));

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("startTime과 endTime의 날짜가 다릅니다");
        }

        @DisplayName("종료 시각이 자정이고 '시작 날짜 = (종료 날짜 -1)'이 아니면 예외를 발생한다")
        @Test
        void validateIsSameDayShouldThrow2() {
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 20, 10, 30),
                    LocalDateTime.of(2024, 4, 20, 0, 0));
            ScheduleDayRequest dayRequest = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest));

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("startTime과 endTime의 날짜가 다릅니다");
        }

        @DisplayName("startTime~endTime이 프로젝트 시간을 벗어나면 예외를 발생한다")
        @Test
        void validateIsAppropriateTimePerSchedule() {
            ScheduleDto scheduleDto1 = new ScheduleDto(
                    LocalDateTime.of(2024, 4, 19, 11, 30),
                    LocalDateTime.of(2024, 4, 19, 14, 30));
            ScheduleDayRequest dayRequest = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
            ScheduleCreateUpdateRequest command = new ScheduleCreateUpdateRequest(List.of(dayRequest));
            Project project = Project.builder()
                    .title("테스트 프로젝트").
                    description("테스트 프로젝트입니다.")
                    .startDate(LocalDate.MIN).endDate(LocalDate.MAX)
                    .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(12, 0))
                    .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                    .coverId(null).color("FFFFFF")
                    .build();

            assertThatThrownBy(() -> validator.validate(command, project))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("프로젝트 수행 시간을 벗어납니다");
        }
    }
}