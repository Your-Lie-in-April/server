package com.appcenter.timepiece.service;

import static java.util.Objects.requireNonNull;

import com.appcenter.timepiece.common.dto.CommonPagingResponse;
import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.common.exception.NotFoundElementException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.Cover;
import com.appcenter.timepiece.domain.Invitation;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Project;
import com.appcenter.timepiece.dto.cover.CoverDataResponse;
import com.appcenter.timepiece.dto.member.MemberResponse;
import com.appcenter.timepiece.dto.project.InvitationLinkResponse;
import com.appcenter.timepiece.dto.project.InvitationResponse;
import com.appcenter.timepiece.dto.project.PinProjectResponse;
import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import com.appcenter.timepiece.dto.project.ProjectResponse;
import com.appcenter.timepiece.dto.project.ProjectThumbnailResponse;
import com.appcenter.timepiece.dto.project.TransferPrivilegeRequest;
import com.appcenter.timepiece.repository.CoverRepository;
import com.appcenter.timepiece.repository.InvitationRepository;
import com.appcenter.timepiece.repository.MemberProjectRepository;
import com.appcenter.timepiece.repository.MemberRepository;
import com.appcenter.timepiece.repository.ProjectRepository;
import com.appcenter.timepiece.repository.ProjectWithCoverDTO;
import com.appcenter.timepiece.util.AESEncoder;
import com.appcenter.timepiece.util.LinkValidTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final CoverRepository coverRepository;
    private final InvitationRepository invitationRepository;
    private final AESEncoder aesEncoder;
    private final ScheduleService scheduleService;
    private final MemberProjectRepository memberProjectRepository;
    private final NotificationService notificationService;


    @Transactional(readOnly = true)
    public ProjectResponse findProject(Long projectId, UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        if (project.getCoverId() == null) {
            return ProjectResponse.of(project, null);
        }
        Cover cover = coverRepository.findById(project.getCoverId()).orElse(null);
        return ProjectResponse.of(project, cover);
    }

    /**
     * @param memberId 자동생성되는 멤버 식별자(PK)
     * @return 메인페이지에 나타나는 프로젝트 썸네일 정보를 담은 dto 리스트를 리턴합니다.
     */
    public CommonPagingResponse<?> findProjects(Integer page, Integer size, Long memberId, UserDetails userDetails) {
        validateMemberIsOwner(memberId, userDetails);
        PageRequest pageable = PageRequest.of(page, size);

        Page<ProjectThumbnailResponse> projectThumbnailResponses = memberProjectRepository.fetchProjectThumbnailResponse(
                pageable, memberId);

        return new CommonPagingResponse<>(page, size, projectThumbnailResponses.getTotalElements(),
                projectThumbnailResponses.getTotalPages(),
                projectThumbnailResponses.getContent());
    }

    /**
     * {@summary 핀 설정된 프로젝트 정보를 조회한다.}
     * <p>조회되는 정보에는 프로젝트 정보 이외에도 이번 주차의 모든 멤버들의 스케줄도 포함된다.
     * 두 개 이상의 프로젝트를 핀 설정할 수 있도록 List 형태로 반환한다. 이 메서드는 내부적으로 ScheduleService 클래스의 findMemebersSchedules를 사용한다.</p>
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
            Long projectId = memberProject.getProjectId();
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));

            if (project.getCoverId() == null) {
                pinProjectResponses.add(PinProjectResponse.of(project, null,
                        scheduleService.findMembersSchedules(projectId, LocalDate.now(), userDetails)));
                continue;
            }
            Cover cover = coverRepository.findById(project.getCoverId()).orElse(null);
            // todo: List<ScheduleWeekResponse>를 생성하는 로직 작성
            pinProjectResponses.add(PinProjectResponse.of(project,
                    ((cover == null) ? null : cover.getCoverImageUrl()),
                    scheduleService.findMembersSchedules(projectId, LocalDate.now(), userDetails)));
        }
        return pinProjectResponses;
    }

    // todo: SchedulService와 중복코드
    private LocalDateTime calculateStartDay(LocalDateTime condition) {
        return condition.minusDays(condition.getDayOfWeek().getValue() % 7);
    }

    @Transactional
    public CommonPagingResponse<?> searchProjects(Integer page, Integer size, Boolean isStored, Long memberId,
                                                  String keyword, UserDetails userDetails) {
        validateMemberIsOwner(memberId, userDetails);

        PageRequest pageable = PageRequest.of(page, size);

        // 프로젝트 및 커버 정보를 함께 조회하는 쿼리로 수정
        Page<ProjectWithCoverDTO> projectPage = projectRepository.searchProjectsWithCover(memberId, keyword, isStored,
                pageable);

        List<ProjectThumbnailResponse> projectThumbnailResponses = projectPage.getContent().stream()
                .map(dto -> ProjectThumbnailResponse.of(
                        dto.getProject(),
                        dto.getThumbnailUrl()))
                .toList();

        return new CommonPagingResponse<>(
                page,
                size,
                projectPage.getTotalElements(),
                projectPage.getTotalPages(),
                projectThumbnailResponses);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findMembers(Long projectId, UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);
        return memberProjectRepository.findAllByProjectId(projectId).stream()
                .map(memberProject -> {
                    Long memberId = requireNonNull(memberProject.getMemberId()); // NPE!
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
                    return MemberResponse.of(member, memberProject);
                }).toList();
    }

    private void validateMemberIsInProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        boolean isExist = memberProjectRepository.existsByMemberIdAndProjectId(memberId, projectId);
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

        Long coverId = null;
        if (request.getCoverImageId() != null && !request.getCoverImageId().isEmpty()) {
            coverId = Long.valueOf(request.getCoverImageId());
        }

        Project project = projectRepository.save(Project.of(request, coverId));
        MemberProject memberProject = MemberProject.of(member, project);
        memberProject.grantPrivilege();

        memberProjectRepository.save(memberProject);
    }

    @Transactional
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

        Long coverId = null;
        if (request.getCoverImageId() != null && !request.getCoverImageId().isEmpty()) {
            coverId = Long.valueOf(request.getCoverImageId());
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));

        project.updateFrom(request, coverId);
    }

    @Transactional
    public void kick(Long projectId, Long memberId, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        if (memberProject.getIsPrivileged()) {
            throw new NotEnoughPrivilegeException(ExceptionMessage.KICK_ADMIN);
        }

        memberProjectRepository.delete(memberProject);
    }

    @Transactional
    public InvitationLinkResponse generateInviteLink(Long projectId, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        Member member = memberRepository.findById(((CustomUserDetails) userDetails).getId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));

        String urlData = projectId
                + "?" + member.getNickname()
                + "?" + LocalDateTime.now().plusDays(LinkValidTime.WEEK.value());

        String url = aesEncoder.encryptAES256(urlData);
        invitationRepository.save(Invitation.of(project, url));

        return InvitationLinkResponse.of(project, url);
    }

    @Transactional
    public void addUserToGroup(String url, UserDetails userDetails) {
        // todo:  Inivation 엔티티 존재 확인 + 어떻게 삭제할 것인지?
        StringTokenizer st = new StringTokenizer(aesEncoder.decryptAES256(url), "?");
        Long projectId = Long.valueOf(st.nextToken());

        st.nextToken();

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
        boolean isExist = memberProjectRepository.existsByMemberIdAndProjectId(memberId, projectId);
        if (isExist) {
            throw new IllegalStateException(ExceptionMessage.DUPLICATE_SIGN_REQUEST.getMessage());
        }
    }

    private void validateRequesterIsPrivileged(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        if (memberProject.getIsPrivileged()) {
            return;
        }
        throw new NotEnoughPrivilegeException(ExceptionMessage.INSUFFICIENT_PRIVILEGE);
    }

    @Transactional
    public void pinProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        memberProject.switchIsPinned();
    }

    @Transactional
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

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));

        notificationService.notifyBecomingOwner(project, toMemberId, fromMemberId);
        memberProjectRepository.save(fromMemberProject);
        memberProjectRepository.save(toMemberProject);
    }

    @Transactional(readOnly = true)
    public CommonPagingResponse<?> findStoredProjects(Integer page, Integer size, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        PageRequest pageable = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findProjectIsStored(memberId, pageable);
        List<Project> projects = projectPage.getContent();

        List<ProjectThumbnailResponse> projectThumbnailResponses = projects.stream().map(p -> {
                            if (p.getCoverId() == null) {
                                return ProjectThumbnailResponse.of(p, null);
                            }
                            Cover cover = coverRepository.findById(p.getCoverId()).orElse(null);
                            return ProjectThumbnailResponse.of(p, ((cover == null) ? null : cover.getThumbnailUrl()));
                        }
                )
                .toList();
        return new CommonPagingResponse<>(page, size, projectPage.getTotalElements(), projectPage.getTotalPages(),
                projectThumbnailResponses);
    }

    public InvitationResponse decodeInviteLink(String url) {
        StringTokenizer st = new StringTokenizer(aesEncoder.decryptAES256(url), "?");
        Long projectId = Long.valueOf(st.nextToken());
        String invitator = st.nextToken();
        LocalDateTime linkTime = LocalDateTime.parse(st.nextToken());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        return InvitationResponse.of(project, invitator, linkTime);
    }

    public CommonPagingResponse<?> getCoverMetadata(Integer page, Integer size) {
        Page<Cover> covers = coverRepository.findAll(PageRequest.of(page, size));
        return new CommonPagingResponse<>(page, size, covers.getTotalElements(), covers.getTotalPages(),
                covers.stream().map(CoverDataResponse::of).toList());
    }
}

