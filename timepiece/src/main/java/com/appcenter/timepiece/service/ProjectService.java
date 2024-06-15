package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.common.exception.NotFoundElementException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.*;
import com.appcenter.timepiece.dto.cover.CoverDataResponse;
import com.appcenter.timepiece.dto.member.MemberResponse;
import com.appcenter.timepiece.dto.project.*;
import com.appcenter.timepiece.repository.*;
import com.appcenter.timepiece.util.AESEncoder;
import com.appcenter.timepiece.util.LinkValidTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final MemberProjectRepository memberProjectRepository;
    private final CoverRepository coverRepository;
    private final InvitationRepository invitationRepository;
    private final AESEncoder aesEncoder;
    private final ScheduleService scheduleService;
    private final NotificationService notificationService;

    public List<ProjectResponse> findAll() {
        return projectRepository.findAllWithCover().stream().map(p ->
                ProjectResponse.of(p,
                        ((p.getCover() == null) ? null : p.getCover().getCoverImageUrl()))).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findProject(Long projectId, UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        Cover cover = project.getCover();
        return ProjectResponse.of(project, (cover == null) ? null : cover.getCoverImageUrl());
    }

    /**
     * @param memberId 자동생성되는 멤버 식별자(PK)
     * @return 메인페이지에 나타나는 프로젝트 썸네일 정보를 담은 dto 리스트를 리턴합니다.
     */
    public List<ProjectThumbnailResponse> findProjects(Integer page, Integer size, Long memberId, UserDetails userDetails) {
        validateMemberIsOwner(memberId, userDetails);
        PageRequest pageable = PageRequest.of(page, size);
        Page<MemberProject> projectPage = memberProjectRepository.findMemberProjectsWithProjectAndCover(pageable, memberId);

        List<MemberProject> projects = projectPage.getContent();
        List<ProjectThumbnailResponse> projectThumbnailResponses = projects.stream()
                .map(MemberProject::getProject)
                .map(p -> ProjectThumbnailResponse.of(p, ((p.getCover() == null) ? null : p.getCover().getCoverImageUrl()))).toList();

        return projectThumbnailResponses;
    }

    /**
     * {@summary 핀 설정된 프로젝트 정보를 조회한다.}
     * <p>조회되는 정보에는 프로젝트 정보 이외에도 이번 주차의 모든 멤버들의 스케줄도 포함된다.
     * 두 개 이상의 프로젝트를 핀 설정할 수 있도록 List 형태로 반환한다.
     * 이 메서드는 내부적으로 ScheduleService 클래스의 findMemebersSchedules를 사용한다.</p>
     *
     * @param memberId
     * @param userDetails
     * @return
     */
    @Transactional
    public List<PinProjectResponse> findPinProjects(Long memberId, UserDetails userDetails) {
        validateMemberIsOwner(memberId, userDetails);

        List<MemberProject> memberProjects = memberProjectRepository.findByMemberIdAndIsPinnedIsTrue(memberId);
        List<PinProjectResponse> pinProjectResponses = new ArrayList<>();

        for (MemberProject memberProject : memberProjects) {
            Project project = memberProject.getProject();
            // todo: List<ScheduleWeekResponse>를 생성하는 로직 작성
            pinProjectResponses.add(PinProjectResponse.of(project, ((project.getCover() == null) ? null : project.getCover().getCoverImageUrl()),
                    scheduleService.findMembersSchedules(project.getId(), LocalDate.now(), userDetails)));
        }
        return pinProjectResponses;
    }

    // todo: SchedulService와 중복코드
    private LocalDateTime calculateStartDay(LocalDateTime condition) {
        return condition.minusDays(condition.getDayOfWeek().getValue() % 7);
    }

    @Transactional
    public List<ProjectThumbnailResponse> searchProjects(Integer page, Integer size, Boolean isStored, Long memberId, String keyword, UserDetails userDetails) {
        validateMemberIsOwner(memberId, userDetails);

        PageRequest pageable = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findProjectByMemberIdAndTitleLikeKeyword(isStored, memberId, keyword, pageable);
        List<Project> projects = projectPage.getContent();
        List<ProjectThumbnailResponse> projectThumbnailResponses = projects.stream()
                .map(p -> ProjectThumbnailResponse.of(p, ((p.getCover() == null) ? null : p.getCover().getCoverImageUrl()))).toList();

        return projectThumbnailResponses;

    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findMembers(Long projectId, UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);
        return memberProjectRepository.findByProjectIdWithMember(projectId).stream()
                .map(memberProject -> {
                    Member member = requireNonNull(memberProject.getMember()); // NPE!
                    return MemberResponse.of(member, memberProject);
                }).toList();
    }

    private void validateMemberIsInProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        boolean isExist = memberProjectRepository.existsByMemberIdAndProjectIdAndProjectIsDeletedIsFalse(memberId, projectId);
        if (!isExist) {
            throw new NotEnoughPrivilegeException(ExceptionMessage.NOT_MEMBER);
        }
    }

    private void validateMemberIsOwner(Long memberId, UserDetails userDetails) {
        if (!memberId.equals(((CustomUserDetails) userDetails).getId())) {
            throw new NotEnoughPrivilegeException(ExceptionMessage.MEMBER_UNAUTHENTICATED);
        }
    }

    public void createProject(ProjectCreateUpdateRequest request, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        Cover cover = coverRepository.findById(Long.valueOf(request.getCoverImageId())).orElse(null);;
        Project project = projectRepository.save(Project.of(request, cover));
        MemberProject memberProject = MemberProject.of(member, project);
        memberProject.grantPrivilege();

        memberProjectRepository.save(memberProject);
    }

    public void deleteProject(Long projectId, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        project.deleteProject();

        projectRepository.save(project);
    }

    @Transactional
    public void updateProject(Long projectId, ProjectCreateUpdateRequest request, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        Cover cover = coverRepository.findById(Long.valueOf(request.getCoverImageId())).orElse(null);;

        project.updateFrom(request, cover);
    }

    public void kick(Long projectId, Long memberId, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        if (memberProject.getIsPrivileged()) {
            throw new NotEnoughPrivilegeException(ExceptionMessage.KICK_ADMIN);
        }

        memberProjectRepository.delete(memberProject);
    }

    public String generateInviteLink(Long projectId, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        Member member = memberRepository.findById(((CustomUserDetails) userDetails).getId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));

        String urlData = new StringBuilder().append(projectId)
                .append("?").append(member.getNickname())
                .append("?").append(LocalDateTime.now().plusDays(LinkValidTime.WEEK.value()))
                .toString();

        String url = aesEncoder.encryptAES256(urlData);
        invitationRepository.save(Invitation.of(project, url));
        return url;
    }

    @Transactional
    public void addUserToGroup(String url, UserDetails userDetails) {
        // todo:  Inivation 엔티티 존재 확인 + 어떻게 삭제할 것인지?
        StringTokenizer st = new StringTokenizer(aesEncoder.decryptAES256(url), "?");
        Long projectId = Long.valueOf(st.nextToken());

        String invitator = st.nextToken();

//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime linkTime = LocalDateTime.parse(st.nextToken());
        if (linkTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(ExceptionMessage.LINK_EXPIRED.getMessage());
        }

        Long memberId = ((CustomUserDetails) userDetails).getId();
        validateJoinIsNotDuplicate(memberId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        MemberProject memberProject = MemberProject.of(member, project);
        memberProjectRepository.save(memberProject);

        notificationService.notifySigning(project, member);
    }

    private void validateJoinIsNotDuplicate(Long memberId, Long projectId) {
        boolean isExist = memberProjectRepository.existsByMemberIdAndProjectIdAndProjectIsDeletedIsFalse(memberId, projectId);
        if (isExist) {
            throw new IllegalStateException(ExceptionMessage.DUPLICATE_SIGN_REQUEST.getMessage());
        }
    }

    private void validateRequesterIsPrivileged(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        if (memberProject.getIsPrivileged()) return;
        throw new NotEnoughPrivilegeException(ExceptionMessage.INSUFFICIENT_PRIVILEGE);
    }

    @Transactional
    public void pinProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        memberProject.switchIsPinned();
    }

    public void goOut(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        if (memberProject.getIsPrivileged()) {
            throw new IllegalStateException(ExceptionMessage.ADMIN_LEAVE.getMessage());
        }
        memberProjectRepository.delete(memberProject);
    }

    @Transactional
    public void transferPrivilege(Long projectId, TransferPrivilegeRequest request, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        Long fromMemberId = ((CustomUserDetails) userDetails).getId();
        Long toMemberId = request.getToMemberId();

        MemberProject fromMemberProject = memberProjectRepository.findByMemberIdAndProjectId(fromMemberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        MemberProject toMemberProject = memberProjectRepository.findByMemberIdAndProjectId(toMemberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        fromMemberProject.releasePrivilege();
        toMemberProject.grantPrivilege();

        memberProjectRepository.save(fromMemberProject);
        memberProjectRepository.save(toMemberProject);

        notificationService.notifyBecomingOwner(toMemberProject.getProject(), toMemberProject.getMember(), fromMemberProject.getMember());
    }

    @Transactional(readOnly = true)
    public List<ProjectThumbnailResponse> findStoredProjects(Integer page, Integer size, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        PageRequest pageable = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findAllByMemberIdWhereIsStored(memberId, pageable);
        List<Project> projects = projectPage.getContent();

        List<ProjectThumbnailResponse> projectThumbnailResponses = projects.stream().map(p ->
                ProjectThumbnailResponse.of(p, ((p.getCover() == null) ? null : p.getCover().getCoverImageUrl()))).toList();
        return projectThumbnailResponses;
    }

    public InvitationResponse decodeInviteLink(String url) {
        StringTokenizer st = new StringTokenizer(aesEncoder.decryptAES256(url), "?");
        Long projectId = Long.valueOf(st.nextToken());
        String invitator = st.nextToken();
        LocalDateTime linkTime = LocalDateTime.parse(st.nextToken());

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        return InvitationResponse.of(project, invitator, linkTime);

    }

    public List<CoverDataResponse> getCoverMetadata(Integer page, Integer size) {
        Page<Cover> covers = coverRepository.findAll(PageRequest.of(page, size));
        return covers.stream().map(CoverDataResponse::of).toList();
    }
}
