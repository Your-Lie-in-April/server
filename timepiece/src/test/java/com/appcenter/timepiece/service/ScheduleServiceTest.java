package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Project;
import com.appcenter.timepiece.dto.schedule.ScheduleCreateUpdateRequest;
import com.appcenter.timepiece.dto.schedule.ScheduleDayRequest;
import com.appcenter.timepiece.dto.schedule.ScheduleDto;
import com.appcenter.timepiece.repository.MemberProjectRepository;
import com.appcenter.timepiece.repository.ProjectRepository;
import com.appcenter.timepiece.repository.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private MemberProjectRepository memberProjectRepository;

    @Mock
    private ProjectRepository projectRepository;

    @DisplayName("ScheduleCreateUpdateRequest가 일주일 분량의 요청이 맞는지 검증한다.")
    @Test
    void validateIsIdenticalWeek() {

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

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1, scheduleDto2)));
        ScheduleDayRequest scheduleDayRequest2 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto3)));
        ScheduleDayRequest scheduleDayRequest3 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto4)));
        ScheduleDayRequest scheduleDayRequest4 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto5)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(1L, List.of(scheduleDayRequest1, scheduleDayRequest2, scheduleDayRequest3, scheduleDayRequest4));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .memberProjects(new ArrayList<>()).invitations(new ArrayList<>())
                .cover(null).color("FFFFFF")
                .build();
        MemberProject memberProject = MemberProject.of(member, project);

        Mockito.when(projectRepository.findById(1L))
                .thenReturn(Optional.of(project));
//        Mockito.when(memberProjectRepository.findByMemberIdAndProjectId(1L, 1L))
//                        .thenReturn(Optional.of(memberProject));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.INVALID_WEEK.getMessage(), exception.getMessage());
    }

    @DisplayName("ScheduleCreateUpdateRequest의 ScheduleDayRequest 간 중복된 날짜가 없는지 검증한다.")
    @Test
    void validateIsIdenticalDayPerWeek() {

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
                LocalDateTime.of(2024, 4, 19, 19, 30),
                LocalDateTime.of(2024, 4, 19, 20, 30));

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1, scheduleDto2)));
        ScheduleDayRequest scheduleDayRequest2 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto3)));
        ScheduleDayRequest scheduleDayRequest3 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto4)));
        ScheduleDayRequest scheduleDayRequest4 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto5)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(1L, List.of(scheduleDayRequest1, scheduleDayRequest2, scheduleDayRequest3, scheduleDayRequest4));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .memberProjects(new ArrayList<>()).invitations(new ArrayList<>())
                .cover(null).color("FFFFFF")
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.DUPLICATE_DATE.getMessage(), exception.getMessage());
    }

    @DisplayName("ScheduleCreateUpdateRequest가 프로젝트 기간 이내인지 검증한다.")
    @Test
    void validateIsAppropriatePeriodPerWeek() {

        ScheduleDto scheduleDto1 = new ScheduleDto(
                LocalDateTime.of(2025, 4, 16, 19, 30),
                LocalDateTime.of(2025, 4, 16, 20, 30));
        ScheduleDto scheduleDto2 = new ScheduleDto(
                LocalDateTime.of(2025, 4, 17, 9, 30),
                LocalDateTime.of(2025, 4, 17, 10, 30));
        ScheduleDto scheduleDto3 = new ScheduleDto(
                LocalDateTime.of(2025, 4, 18, 19, 30),
                LocalDateTime.of(2025, 4, 18, 21, 30));
        ScheduleDto scheduleDto4 = new ScheduleDto(
                LocalDateTime.of(2025, 4, 19, 8, 30),
                LocalDateTime.of(2025, 4, 19, 9, 0));
        ScheduleDto scheduleDto5 = new ScheduleDto(
                LocalDateTime.of(2025, 4, 19, 9, 30),
                LocalDateTime.of(2025, 4, 19, 10, 30));

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1)));
        ScheduleDayRequest scheduleDayRequest2 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto2)));
        ScheduleDayRequest scheduleDayRequest3 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto3)));
        ScheduleDayRequest scheduleDayRequest4 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto4, scheduleDto5)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(1L, List.of(scheduleDayRequest1, scheduleDayRequest2, scheduleDayRequest3, scheduleDayRequest4));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .memberProjects(new ArrayList<>()).invitations(new ArrayList<>())
                .cover(null).color("FFFFFF")
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.INVALID_PROJECT_PERIOD.getMessage(), exception.getMessage());
    }

    @DisplayName("ScheduleDayRequest의 모든 ScheduleDto 날짜가 동일한지 검증한다.")
    @Test
    void validateIsIdenticalDay() {

        ScheduleDto scheduleDto1 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 9, 30),
                LocalDateTime.of(2024, 4, 19, 10, 30));
        ScheduleDto scheduleDto2 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 18, 8, 30),
                LocalDateTime.of(2024, 4, 18, 9, 0));
        ScheduleDto scheduleDto3 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 16, 19, 30),
                LocalDateTime.of(2024, 4, 16, 20, 30));
        ScheduleDto scheduleDto4 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 17, 9, 30),
                LocalDateTime.of(2024, 4, 17, 10, 30));
        ScheduleDto scheduleDto5 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 20, 19, 30),
                LocalDateTime.of(2024, 4, 20, 21, 30));

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1, scheduleDto2)));
        ScheduleDayRequest scheduleDayRequest2 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto3)));
        ScheduleDayRequest scheduleDayRequest3 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto4)));
        ScheduleDayRequest scheduleDayRequest4 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto5)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(1L, List.of(scheduleDayRequest1, scheduleDayRequest2, scheduleDayRequest3, scheduleDayRequest4));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .memberProjects(new ArrayList<>()).invitations(new ArrayList<>())
                .cover(null).color("FFFFFF")
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.INVALID_DATE.getMessage(), exception.getMessage());
    }

    @DisplayName("ScheduleDayRequest가 프로젝트 수행 요일인지 검증한다.")
    @Test
    void validateIsAppropriateDayOfWeekPerDay() {
    }

    @DisplayName("ScheduleDayRequest의 ScheduleDto 간 시간 중복/교차가 없는지 검증한다.")
    @Test
    void validateDuplicateSchedulePerDay() {
        ScheduleDto scheduleDto1 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 9, 30),
                LocalDateTime.of(2024, 4, 19, 10, 30));
        ScheduleDto scheduleDto2 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 10, 30),
                LocalDateTime.of(2024, 4, 19, 20, 0));
        ScheduleDto scheduleDto3 = new ScheduleDto(
                LocalDateTime.of(2024, 4, 19, 19, 30),
                LocalDateTime.of(2024, 4, 19, 20, 30));

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(
                new ArrayList<>(List.of(scheduleDto1, scheduleDto2, scheduleDto3)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(1L, List.of(scheduleDayRequest1));
        Member member =
                new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .memberProjects(new ArrayList<>()).invitations(new ArrayList<>())
                .cover(null).color("FFFFFF")
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class, () ->
                scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.INTERSECT_TIME.getMessage(), exception.getMessage());
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

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1, scheduleDto2, scheduleDto3)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(1L, List.of(scheduleDayRequest1));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .memberProjects(new ArrayList<>()).invitations(new ArrayList<>())
                .cover(null).color("FFFFFF")
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
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

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1, scheduleDto2, scheduleDto3)));


        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(1L, List.of(scheduleDayRequest1));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .memberProjects(new ArrayList<>()).invitations(new ArrayList<>())
                .cover(null).color("FFFFFF")
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
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

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1, scheduleDto2, scheduleDto3)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(1L, List.of(scheduleDayRequest1));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.MIN).endTime(LocalTime.MAX)
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .memberProjects(new ArrayList<>()).invitations(new ArrayList<>())
                .cover(null).color("FFFFFF")
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
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

        ScheduleDayRequest scheduleDayRequest1 = new ScheduleDayRequest(new ArrayList<>(List.of(scheduleDto1, scheduleDto2, scheduleDto3)));

        ScheduleCreateUpdateRequest scheduleCreateUpdateRequest =
                new ScheduleCreateUpdateRequest(1L, List.of(scheduleDayRequest1));
        Member member = new Member(null, "namu", "namu2024@gmail.com", "", "", List.of("ROLE_USER"));
        Project project = Project.builder()
                .title("test").description("설명")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2025, 1, 1))
                .startTime(LocalTime.of(9, 0)).endTime(LocalTime.of(22, 0))
                .daysOfWeek(Arrays.stream(DayOfWeek.values()).collect(Collectors.toSet()))
                .memberProjects(new ArrayList<>()).invitations(new ArrayList<>())
                .cover(null).color("FFFFFF")
                .build();

        Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> scheduleService.createSchedule(scheduleCreateUpdateRequest, 1L, CustomUserDetails.from(member)));
        assertEquals(ExceptionMessage.INVALID_PROJECT_TIME.getMessage(), exception.getMessage());
    }
}