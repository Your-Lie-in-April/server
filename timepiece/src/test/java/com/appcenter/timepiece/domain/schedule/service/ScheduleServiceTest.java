package com.appcenter.timepiece.domain.schedule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.domain.project.entity.Project;
import com.appcenter.timepiece.domain.project.repository.MemberProjectRepository;
import com.appcenter.timepiece.domain.project.repository.ProjectRepository;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleCreateUpdateRequest;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleDayRequest;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleDto;
import com.appcenter.timepiece.domain.schedule.repository.ScheduleRepository;
import com.appcenter.timepiece.global.exception.ExceptionMessage;
import com.appcenter.timepiece.global.security.CustomUserDetails;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("ScheduleService 테스트")
@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Spy
    private ScheduleCreateUpdateValidator scheduleValidator;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private MemberProjectRepository memberProjectRepository;

    @Mock
    private ProjectRepository projectRepository;


    @DisplayName("ScheduleDayRequest가 프로젝트 수행 요일인지 검증한다.")
    @Test
    void validateIsAppropriateDayOfWeekPerDay() {
    }

    @DisplayName("ScheduleDto의 start/end 시간이 30분 단위임을 검증한다.")
    @Test
    void validateIsMultipleOfHalfHourPerSchedule() {
        ScheduleDto scheduleDto1 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 9, 20),
                LocalDateTime.of(2024, 4, 19, 10, 30));
        ScheduleDto scheduleDto2 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 10, 30),
                LocalDateTime.of(2024, 4, 19, 12, 0));
        ScheduleDto scheduleDto3 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 19, 30),
                LocalDateTime.of(2024, 4, 19, 20, 30));

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(
                new ArrayList<>(List.of(scheduleDto1, scheduleDto2, scheduleDto3)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(List.of(scheduleDayRequest1));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .coverId(null).color("FFFFFF")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.INVALID_TIME_UNIT.getMessage(), exception.getMessage());
    }

    @DisplayName("ScheduleDto에서 start 시간 < end 시간임을 검증한다.")
    @Test
    void validateTimeSequencePerSchedule() {
        ScheduleDto scheduleDto1 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 10, 30),
                LocalDateTime.of(2024, 4, 19, 9, 30));
        ScheduleDto scheduleDto2 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 10, 30),
                LocalDateTime.of(2024, 4, 19, 12, 0));
        ScheduleDto scheduleDto3 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 19, 30),
                LocalDateTime.of(2024, 4, 19, 20, 30));

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(
                new ArrayList<>(List.of(scheduleDto1, scheduleDto2, scheduleDto3)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(List.of(scheduleDayRequest1));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .coverId(null).color("FFFFFF")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.INVALID_TIME_SEQUENCE.getMessage(), exception.getMessage());
    }

    @DisplayName("ScheduleDto의 start 날짜와 end 날짜가 같은지 검증한다.")
    @Test
    void validateIsSameDayPerSchedule() {
        ScheduleDto scheduleDto1 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 9, 30),
                LocalDateTime.of(2024, 4, 19, 10, 30));
        ScheduleDto scheduleDto2 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 10, 30),
                LocalDateTime.of(2024, 4, 19, 12, 0));
        ScheduleDto scheduleDto3 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 19, 30),
                LocalDateTime.of(2024, 4, 20, 20, 30));

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(
                new ArrayList<>(List.of(scheduleDto1, scheduleDto2, scheduleDto3)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(List.of(scheduleDayRequest1));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .coverId(null).color("FFFFFF")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.IS_NOT_SAME_DAY.getMessage(), exception.getMessage());
    }

    @DisplayName("ScheduleDto의 startTime~endTime이 프로젝트 시간 내인지 검증한다.")
    @Test
    void validateIsAppropriateTimePerSchedule() {
        ScheduleDto scheduleDto1 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 9, 30),
                LocalDateTime.of(2024, 4, 19, 10, 30));
        ScheduleDto scheduleDto2 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 10, 30),
                LocalDateTime.of(2024, 4, 19, 12, 0));
        ScheduleDto scheduleDto3 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 19, 30),
                LocalDateTime.of(2024, 4, 19, 22, 30));

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(
                new ArrayList<>(List.of(scheduleDto1, scheduleDto2, scheduleDto3)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(List.of(scheduleDayRequest1));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(22, 0))
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .coverId(null).color("FFFFFF")
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class,
                () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.INVALID_PROJECT_TIME.getMessage(), exception.getMessage());
    }
}