package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.common.exception.NotFoundElementException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Schedule;
import com.appcenter.timepiece.dto.schedule.*;
import com.appcenter.timepiece.repository.MemberProjectRepository;
import com.appcenter.timepiece.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final MemberProjectRepository memberProjectRepository;

    /**
     * {@summary 프로젝트 내 모든 멤버의 스케줄을 조회한다(본인포함)}
     * <p>자신을 포함한 모든 멤버의 스케줄을 조회한다.
     * 모든 프로젝트 멤버의 스케줄을 전체 조회한 후,스케줄 중 멤버별로 중복되는 요일을 필터링,
     * 자신의 스케줄을 필터링하여 요일별로 묶어 반환한다. </p>
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
            throw new NotEnoughPrivilegeException("속하지 않은 프로젝트 정보를 조회할 수 없습니다.");
        }
    }

    /**
     * {@summary 스케줄 생성}
     * @param request
     * @param projectId
     * @param userDetails
     */
    @Transactional
    public void createSchedule(ScheduleCreateUpdateRequest request, Long projectId, UserDetails userDetails) {
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
     * @param request
     * @param projectId
     * @param userDetails
     */
    public void editSchedule(ScheduleCreateUpdateRequest request, Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        // todo: IndexOutOfBoundsException!! 발생 가능
        LocalDateTime sundayOfWeek = calculateStartDay(request.getSchedule().get(0).getSchedule().get(0).getStartTime());
        scheduleRepository.deleteMemberSchedulesBetween(memberProject.getId(), sundayOfWeek, sundayOfWeek.plusDays(7));

        List<Schedule> schedulesToSave = request.getSchedule().stream()
                .flatMap(scheduleDayRequest -> scheduleDayRequest.getSchedule().stream())
                .map(scheduleDto -> Schedule.of(scheduleDto, memberProject))
                .toList();

        scheduleRepository.saveAll(schedulesToSave);
    }

    // todo: ProjectService와 중복코드
    private LocalDateTime calculateStartDay(LocalDateTime condition) {
        return condition.minusDays(condition.getDayOfWeek().getValue() % 7);
    }


    /**
     * {@summary start <= 날짜 < end에 속하는 스케줄 삭제}
     * @param request
     * @param projectId
     * @param userDetails
     */
    public void deleteSchedule(ScheduleDeleteRequest request, Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        // todo: 현재 endDate 날짜에 속하는 스케줄은 삭제되지 않음
        scheduleRepository.deleteMemberSchedulesBetween(memberProject.getId(),
                LocalDateTime.of(request.getStartDate(), LocalTime.MIN),
                LocalDateTime.of(request.getEndDate(), LocalTime.MIN));
    }

}
