package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.common.exception.NotFoundElementException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Project;
import com.appcenter.timepiece.domain.Schedule;
import com.appcenter.timepiece.dto.schedule.*;
import com.appcenter.timepiece.repository.MemberProjectRepository;
import com.appcenter.timepiece.repository.ProjectRepository;
import com.appcenter.timepiece.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.ErrorMessage;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberProjectRepository memberProjectRepository;
    private final ProjectRepository projectRepository;

    /**
     * {@summary 프로젝트 내 모든 멤버의 스케줄을 조회한다(본인포함)}
     * <p>자신을 포함한 모든 멤버의 스케줄을 조회한다.
     * 모든 프로젝트 멤버의 스케줄을 전체 조회한 후,스케줄 중 멤버별로 중복되는 요일을 필터링,
     * 자신의 스케줄을 필터링하여 요일별로 묶어 반환한다. </p>
     *
     * @param projectId
     * @param condition
     * @param userDetails
     * @return
     */
    @Transactional(readOnly = true)
    public List<ScheduleWeekResponse> findMembersSchedules(Long projectId, LocalDate condition, UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);
        List<MemberProject> memberProjects = memberProjectRepository.findAllByProjectId(projectId);
        LocalDateTime sundayOfWeek = calculateStartDay(LocalDateTime.of(condition, LocalTime.MIN));

        List<Schedule> schedules = scheduleRepository.findMembersWeekSchedule(memberProjects.stream().map(MemberProject::getId).toList(), sundayOfWeek, sundayOfWeek.plusDays(7));
        return memberProjects.stream().map(memberProject ->
                        new ScheduleWeekResponse(memberProject.getMember().getNickname(),
                                schedules.stream()
                                        .filter(schedule -> schedule.getMemberProject().getId().equals(memberProject.getId()))
                                        .map(schedule -> schedule.getStartTime().getDayOfWeek())
                                        .distinct()
                                        .map(dayOfWeek -> ScheduleDayResponse.of(dayOfWeek,
                                                schedules.stream()
                                                        .filter(schedule -> schedule.getMemberProject().getId().equals(memberProject.getId()))
                                                        .filter(schedule -> schedule
                                                                .getStartTime()
                                                                .getDayOfWeek()
                                                                .equals(dayOfWeek))
                                                        .map(ScheduleDto::from)
                                                        .collect(Collectors.toList())))
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    /**
     * {@summary 특정 프로젝트-사용자의 condition 날짜가 포함된 주차 스케줄 조회}
     * <p>condition이 속한 주차(= 해당 주차의 일요일 ~ 토요일까지)의 사용자 스케줄을 조회한다.
     * 해당 기간동안의 사용자의 스케줄을 전부 조회한 후 날짜별로 묶어 반환한다.
     *
     * @param projectId
     * @param memberId
     * @param condition
     * @param userDetails
     * @return
     */
    @Transactional(readOnly = true)
    public ScheduleWeekResponse findSchedule(Long projectId, Long memberId, LocalDate condition, UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        LocalDateTime sundayOfWeek = calculateStartDay(LocalDateTime.of(condition, LocalTime.MIN));

        List<Schedule> schedules = scheduleRepository.findMemberWeekSchedule(memberProject.getId(), sundayOfWeek, sundayOfWeek.plusDays(7));
        return new ScheduleWeekResponse(memberProject.getMember().getNickname(), schedules.stream()
                .map(schedule -> schedule.getStartTime().getDayOfWeek())
                .distinct()
                .map(dayOfWeek -> ScheduleDayResponse.of(dayOfWeek, schedules.stream()
                        .filter(schedule -> schedule
                                .getStartTime()
                                .getDayOfWeek()
                                .equals(dayOfWeek))
                        .map(ScheduleDto::from)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList()));
    }

    // todo: ProjectService와 중복코드
    private void validateMemberIsInProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        boolean isExist = memberProjectRepository.existsByMemberIdAndProjectId(memberId, projectId);
        if (!isExist) {
            throw new NotEnoughPrivilegeException(ExceptionMessage.NOT_MEMBER);
        }
    }

    /**
     * {@summary 스케줄 생성}
     *
     * @param request
     * @param projectId
     * @param userDetails
     */
    @Transactional
    public void createSchedule(ScheduleCreateUpdateRequest request, Long projectId, UserDetails userDetails) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        validateScheduleCreateUpdateRequest(request, project);

        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        List<Schedule> schedulesToSave = request.getSchedule().stream()
                .flatMap(scheduleDayRequest -> scheduleDayRequest.getSchedule().stream())
                .map(scheduleDto -> Schedule.of(scheduleDto, memberProject))
                .toList();

        scheduleRepository.saveAll(schedulesToSave);
    }

    /**
     * {@summary 기존 스케줄 삭제 후 새 스케줄 저장}
     * <p>request에 존재하는 첫번째 스케줄의 날짜로, 해당 주차의 첫번째 요일(일요일)을 계산,
     * 계산한 첫번째 요일 ~ (첫번째 요일 + 7)일의 기간에 속하는 스케줄을 DB에서 삭제하고 request로 받은
     * 수정 후 스케줄을 저장
     *
     * @param request
     * @param projectId
     * @param userDetails
     */
    public void editSchedule(ScheduleCreateUpdateRequest request, Long projectId, UserDetails userDetails) {
        Project project = projectRepository.findById(projectId)
                        .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        validateScheduleCreateUpdateRequest(request, project);

        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        // todo: IndexOutOfBoundsException!! 발생 가능 -> Not Null, Not Empty하면 될 듯?
        LocalDateTime sundayOfWeek = calculateStartDay(request.getSchedule().get(0).getSchedule().get(0).getStartTime());
        scheduleRepository.deleteMemberSchedulesBetween(memberProject.getId(), sundayOfWeek, sundayOfWeek.plusDays(7));

        List<Schedule> schedulesToSave = request.getSchedule().stream()
                .flatMap(scheduleDayRequest -> scheduleDayRequest.getSchedule().stream())
                .map(scheduleDto -> Schedule.of(scheduleDto, memberProject))
                .toList();

        scheduleRepository.saveAll(schedulesToSave);
    }

    private LocalDateTime calculateStartDay(LocalDateTime condition) {
        return condition.minusDays(condition.getDayOfWeek().getValue() % 7);
    }


    /**
     * {@summary start <= 날짜 < end}에 속하는 스케줄 삭제
     *
     * @param request
     * @param projectId
     * @param userDetails
     */
    public void deleteSchedule(ScheduleDeleteRequest request, Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        scheduleRepository.deleteMemberSchedulesBetween(memberProject.getId(),
                LocalDateTime.of(request.getStartDate(), LocalTime.MIN),
                LocalDateTime.of(request.getEndDate(), LocalTime.MIN));
    }

    /**
     * ScheduleCreateUpdateRequest에 대한 모든 유효성 검사를 위임하는 메서드<br>
     * Week, Day 범위로 검증을 위임한다.
     * @param req ScheduleCreateUpdateRequest
     * @param project Project
     */
    private void validateScheduleCreateUpdateRequest(ScheduleCreateUpdateRequest req, Project project) {
        validateScheduleWeekRequest(req, project);
        for (ScheduleDayRequest scheduleDayRequest : req.getSchedule()) {
            validateDayAndLowLevelRequest(scheduleDayRequest, project);
        }
    }

    /**
     * ScheduleCreateUpdateRequest를 일(Day) 단위 및 ScheduleDto 단위 검증으로 위임한다.
     * @param req ScheduleDayRequest
     * @param project Project
     */
    private void validateDayAndLowLevelRequest(ScheduleDayRequest req, Project project) {
        validateScheduleDayRequest(req, project);
        for (ScheduleDto scheduleDto : req.getSchedule()) {
            validateScheduleDto(scheduleDto, project);
        }
    }

    /**
     * ScheduleCreateUpdateRequest Week 단위 검증 <br>
     * 수행목록 <br>
     * 1. validateIsIdenticalWeek - 일주일(일-토요일) 단위의 요청이 맞는지 검사 <br>
     * 2. validateIsIdenticalDayPerWeek - 중복된 날짜의 요청이 있는지 검사 <br>
     * 3. validateIsAppropriatePeriodPerWeek - (생성 시 정했던)프로젝트 기간 내인지 검사
     * @param req ScheduleCreateUpdateRequest
     * @param project Project
     */
    private void validateScheduleWeekRequest(ScheduleCreateUpdateRequest req, Project project) {
        validateIsIdenticalWeek(req);
        validateIsIdenticalDayPerWeek(req);
        validateIsAppropriatePeriodPerWeek(req, project);
    }

    // todo: LocalDate 간 equals 확인
    private void validateIsIdenticalWeek(ScheduleCreateUpdateRequest req) {
        LocalDate criteria = calculateStartDay(req.getSchedule().get(0).getSchedule().get(0).getStartTime()).toLocalDate();
        for (ScheduleDayRequest scheduleDayRequest : req.getSchedule()) {
            LocalDateTime validTarget = calculateStartDay(scheduleDayRequest.getSchedule().get(0).getStartTime());
            if (!Objects.equals(criteria, validTarget.toLocalDate())) {
                throw new IllegalArgumentException(ExceptionMessage.INVALID_WEEK.getMessage());
            }
        }
    }

    // todo: LocalDate끼리 Set에서 contain 검사되는지 확인
    private void validateIsIdenticalDayPerWeek(ScheduleCreateUpdateRequest req) {
        Set<LocalDate> set = new HashSet<>();
        for (ScheduleDayRequest scheduleDayRequest : req.getSchedule()) {
            if (set.contains(scheduleDayRequest.getSchedule().get(0).getStartTime().toLocalDate())) {
                throw new IllegalArgumentException(ExceptionMessage.DUPLICATE_DATE.getMessage());
            }
            set.add(req.getSchedule().get(0).getSchedule().get(0).getStartTime().toLocalDate());
        }
    }

    private void validateIsAppropriatePeriodPerWeek(ScheduleCreateUpdateRequest req, Project project) {
        LocalDate criteria = calculateStartDay(req.getSchedule().get(0).getSchedule().get(0).getStartTime()).toLocalDate();
        if (criteria.isBefore(project.getStartDate()) || criteria.plusDays(6).isAfter(project.getEndDate())) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_PROJECT_PERIOD.getMessage());
        }
    }


    /**
     * ScheduleCreateUpdateRequest 일(Day) 단위 검증 <br>
     * 수행목록 <br>
     * 1. validateIsIdenticalDay - 모든 ScheduleDto의 동일한 날짜인지 검사<br>
     * 2. validateDuplicateSchedulePerDay - ScheduleDto 간 요청 시간이 중복/교차되는지 검사<br>
     * 3. validateIsAppropriateDayOfWeekPerDay - (생성 시 정했던)프로젝트 요일인지 검사
     * @param req ScheduleDayRequest
     * @param project Project
     */
    private void validateScheduleDayRequest(ScheduleDayRequest req, Project project) {
        validateIsIdenticalDay(req);
        validateDuplicateSchedulePerDay(req);
        validateIsAppropriateDayOfWeekPerDay(req, project);
    }

    // todo: 하루에 포함된 모든 ScheduleDto가 같은 날짜인지
    // todo: count = 1확인
    private void validateIsIdenticalDay(ScheduleDayRequest req) {
        if (req.getSchedule().stream()
                    .map(ScheduleDto::getStartTime)
                    .map(LocalDateTime::toLocalDate).distinct().count() != 1L) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_DATE.getMessage());
        }
    }

    private void validateDuplicateSchedulePerDay(ScheduleDayRequest req) {
        req.getSchedule().sort(Comparator.comparing(ScheduleDto::getStartTime));
        LocalDateTime before = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        for (ScheduleDto scheduleDto : req.getSchedule()) {
            if (before.isAfter(scheduleDto.getStartTime())) {
                throw new IllegalArgumentException("");
            }
            before = scheduleDto.getEndTime();
        }
    }

    private void validateIsAppropriateDayOfWeekPerDay(ScheduleDayRequest req, Project project) {
        Set<DayOfWeek> dayOfWeeks = project.getDaysOfWeek();
        DayOfWeek day= req.getSchedule().get(0).getStartTime().getDayOfWeek();
        if (!dayOfWeeks.contains(day)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_PROJECT_DAY_OF_WEEK.getMessage());
        }
    }



    /**
     * ScheduleCreateUpdateRequest ScheduleDto 단위 검증 <br>
     * 수행목록 <br>
     * 1. validateIsMultipleOfHalfHourPerSchedule - startTime, endTime이 30분 단위인지 검사 <br>
     * 2. validateTimeSequencePerSchedule - startTime < endTime을 만족하는지 검사 <br>
     * 3. validateIsSameDayPerSchedule - startDate == endDate를 만족하는지 검사 <br>
     * 4. validateIsAppropriateTimePerSchedule - (생성 시 정했던)프로젝트 시간 내인지 검사
     * @param req ScheduleDto
     * @param project Project
     */
    private void validateScheduleDto(ScheduleDto req, Project project) {
        LocalDateTime startDateTime = req.getStartTime();
        LocalDateTime endDateTime = req.getEndTime();

        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();

        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = endDateTime.toLocalTime();

        validateIsMultipleOfHalfHourPerSchedule(startTime, endTime);
        validateTimeSequencePerSchedule(startTime, endTime);
        validateIsSameDayPerSchedule(startDate, endDate);
        validateIsAppropriateTimePerSchedule(startTime, endTime, project);
    }

    private void validateIsMultipleOfHalfHourPerSchedule(LocalTime startTime, LocalTime endTime) {
        if ((startTime.getMinute() % 30 != 0) || (endTime.getMinute() % 30 != 0)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_TIME_UNIT.getMessage());
        }
    }

    private void validateTimeSequencePerSchedule(LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_TIME_SEQUENCE.getMessage());
        }
    }

    private void validateIsSameDayPerSchedule(LocalDate startDate, LocalDate endDate) {
        if (!Objects.equals(startDate, endDate)) {
            throw new IllegalArgumentException(ExceptionMessage.IS_NOT_SAME_DAY.getMessage());
        }
    }

    private void validateIsAppropriateTimePerSchedule(LocalTime startTime, LocalTime endTime, Project project) {
        if (startTime.isBefore(project.getStartTime()) || endTime.isAfter(project.getEndTime())) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_PROJECT_TIME.getMessage());
        }
    }
}
