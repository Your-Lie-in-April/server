package com.appcenter.timepiece.domain.schedule.service;

import static com.appcenter.timepiece.domain.schedule.util.ScheduleUtil.calculateEndDay;
import static com.appcenter.timepiece.domain.schedule.util.ScheduleUtil.calculateStartDay;

import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.domain.member.repository.MemberRepository;
import com.appcenter.timepiece.domain.notification.service.NotificationService;
import com.appcenter.timepiece.domain.project.entity.MemberProject;
import com.appcenter.timepiece.domain.project.entity.Project;
import com.appcenter.timepiece.domain.project.repository.MemberProjectRepository;
import com.appcenter.timepiece.domain.project.repository.ProjectRepository;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleCreateUpdateRequest;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleDeleteRequest;
import com.appcenter.timepiece.domain.schedule.dto.ScheduleWeekResponse;
import com.appcenter.timepiece.domain.schedule.entity.Schedule;
import com.appcenter.timepiece.domain.schedule.entity.ScheduleCollection;
import com.appcenter.timepiece.domain.schedule.repository.ScheduleRepository;
import com.appcenter.timepiece.global.exception.ExceptionMessage;
import com.appcenter.timepiece.global.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.global.exception.NotFoundElementException;
import com.appcenter.timepiece.global.security.CustomUserDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {

    private final NotificationService notificationService;
    private final MemberProjectRepository memberProjectRepository;
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleValidator scheduleValidator;

    /**
     * {@summary 프로젝트 내 모든 멤버의 스케줄을 조회한다(본인포함)}
     * <p>자신을 포함한 모든 멤버의 스케줄을 조회한다.
     * 모든 프로젝트 멤버의 스케줄을 전체 조회한 후,스케줄 중 멤버별로 중복되는 요일을 필터링, 자신의 스케줄을 필터링하여 요일별로 묶어 반환한다. </p>
     */
    @Transactional(readOnly = true)
    public List<ScheduleWeekResponse> findMembersSchedules(Long projectId, LocalDate condition,
                                                           UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);
        List<MemberProject> memberProjects = memberProjectRepository.findAllByProjectId(projectId);

        List<Long> memberProjectIds = memberProjects.stream().map(MemberProject::getId).toList();
        return getScheduleWeekResponses(condition, memberProjects, memberProjectIds);
    }

    private List<ScheduleWeekResponse> getScheduleWeekResponses(LocalDate condition, List<MemberProject> memberProjects,
                                                                List<Long> memberProjectIds) {
        LocalDateTime sundayOfWeek = calculateStartDay(condition);
        LocalDateTime endOfWeek = calculateEndDay(sundayOfWeek);

        List<Schedule> schedules = scheduleRepository.findMembersWeekSchedule(memberProjectIds, sundayOfWeek,
                endOfWeek);

        Map<Long, ScheduleCollection> scheduleCollectionsByMemberProjectId = schedules.stream()
                .collect(Collectors.groupingBy(schedule -> schedule.getMemberProjectId(),
                        Collectors.collectingAndThen(Collectors.toList(), ScheduleCollection::from)));

        return memberProjects.stream()
                .map(memberProject -> {
                    ScheduleCollection scheduleCollection = scheduleCollectionsByMemberProjectId.get(
                            memberProject.getId());
                    if (scheduleCollection != null) {
                        return new ScheduleWeekResponse(memberProject.getNickname(),
                                scheduleCollection.toScheduleDayResponses());
                    }
                    return null;
                }).filter(Objects::nonNull).toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleWeekResponse> findMembersSchedulesWithoutMe(Long projectId, LocalDate condition,
                                                                    UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);

        List<MemberProject> memberProjects = memberProjectRepository.findAllByProjectId(projectId);
        MemberProject me = memberProjectRepository.findByMemberIdAndProjectId(((CustomUserDetails) userDetails).getId(),
                projectId).get();
        memberProjects.remove(me); // 본인 제외, 같은 트랜잭션 내에서 같은 객체를 공유할 것임.

        List<Long> memberProjectIds = memberProjects.stream().map(MemberProject::getId).collect(Collectors.toList());
        return getScheduleWeekResponses(condition, memberProjects, memberProjectIds);
    }

    /**
     * {@summary 특정 프로젝트-사용자의 condition 날짜가 포함된 주차 스케줄 조회}
     * <p>condition이 속한 주차(= 해당 주차의 일요일 ~ 토요일까지)의 사용자 스케줄을 조회한다.
     * 해당 기간동안의 사용자의 스케줄을 전부 조회한 후 날짜별로 묶어 반환한다.
     */
    @Transactional(readOnly = true)
    public ScheduleWeekResponse findSchedule(Long projectId, Long memberId, LocalDate condition,
                                             UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        LocalDateTime sundayOfWeek = calculateStartDay(condition);
        LocalDateTime endOfWeek = calculateEndDay(sundayOfWeek);
        List<Schedule> schedules = scheduleRepository.findMemberWeekSchedule(memberProject.getId(), sundayOfWeek,
                endOfWeek);

        ScheduleCollection scheduleCollection = ScheduleCollection.from(schedules);
        return new ScheduleWeekResponse(memberProject.getNickname(),
                scheduleCollection.toScheduleDayResponses());
    }

    private void validateMemberIsInProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        boolean isExist = memberProjectRepository.existsByMemberIdAndProjectId(memberId, projectId);
        if (!isExist) {
            throw new NotEnoughPrivilegeException(ExceptionMessage.NOT_MEMBER);
        }
    }

    @Transactional
    public void createSchedule(ScheduleCreateUpdateRequest request, Long projectId, UserDetails userDetails) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        scheduleValidator.validate(request, project);

        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        List<Schedule> schedulesToSave = request.getSchedule().stream()
                .flatMap(scheduleDayRequest -> scheduleDayRequest.getSchedule().stream())
                .map(scheduleDto -> Schedule.of(scheduleDto, memberProject))
                .toList();

        scheduleRepository.saveAll(schedulesToSave);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        notificationService.notifyScheduleChanging(project, member);
    }


    /**
     * {@summary 기존 스케줄 삭제 후 새 스케줄 저장}
     * <p>request에 존재하는 첫번째 스케줄의 날짜로, 해당 주차의 첫번째 요일(일요일)을 계산,
     * 계산한 첫번째 요일 ~ (첫번째 요일 + 7)일의 기간에 속하는 스케줄을 DB에서 삭제하고 request로 받은 수정 후 스케줄을 저장
     */
    @Transactional
    public void editSchedule(ScheduleCreateUpdateRequest request, Long projectId, UserDetails userDetails) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        scheduleValidator.validate(request, project);

        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        LocalDateTime sundayOfWeek = calculateStartDay(
                request.getSchedule().get(0).getSchedule().get(0).getStartTime());
        scheduleRepository.deleteMemberSchedulesBetween(memberProject.getId(), sundayOfWeek,
                calculateEndDay(sundayOfWeek));

        List<Schedule> schedulesToSave = request.getSchedule().stream()
                .flatMap(scheduleDayRequest -> scheduleDayRequest.getSchedule().stream())
                .map(scheduleDto -> Schedule.of(scheduleDto, memberProject))
                .toList();

        scheduleRepository.saveAll(schedulesToSave);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        notificationService.notifyScheduleChanging(project, member);
    }


    /**
     * {@summary start <= 날짜 < end}에 속하는 스케줄 삭제
     */
    @Transactional
    public void deleteSchedule(ScheduleDeleteRequest request, Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        scheduleRepository.deleteMemberSchedulesBetween(memberProject.getId(),
                LocalDateTime.of(request.getStartDate(), LocalTime.MIN),
                LocalDateTime.of(request.getEndDate(), LocalTime.MIN));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        notificationService.notifyScheduleChanging(project, member);
    }
}
