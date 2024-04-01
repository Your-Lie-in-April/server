package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.*;
import com.appcenter.timepiece.dto.member.MemberResponse;
import com.appcenter.timepiece.dto.project.*;
import com.appcenter.timepiece.repository.*;
import com.appcenter.timepiece.util.AESEncoder;
import com.appcenter.timepiece.util.LinkValidTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public List<ProjectResponse> findAll() {
        return projectRepository.findAllWithCover().stream().map(p ->
                ProjectResponse.of(p,
                        ((p.getCover() == null) ? null : p.getCover().getCoverImageUrl()))).toList();
    }

    /**
     * @param memberId 자동생성되는 멤버 식별자(PK)
     * @return 메인페이지에 나타나는 프로젝트 썸네일 정보를 담은 dto 리스트를 리턴합니다.
     */
    public List<ProjectThumbnailResponse> findProjects(Long memberId) {
        return memberProjectRepository.findMemberProjectsWithProjectAndCover(memberId).stream().map(MemberProject::getProject)
                .map(p -> ProjectThumbnailResponse.of(p, ((p.getCover() == null) ? null : p.getCover().getCoverImageUrl()))).toList();
    }

    @Transactional
    public List<PinProjectResponse> findPinProjects(Long memberId) {
        List<MemberProject> memberProjects = memberProjectRepository.findByMemberIdAndIsPinnedIsTrue(memberId);
        List<PinProjectResponse> pinProjectResponses = new ArrayList<>();

        for (MemberProject memberProject : memberProjects) {
            Project project = memberProject.getProject();
            // todo: List<ScheduleWeekResponse>를 생성하는 로직 작성
//            pinProjectResponses.add(PinProjectResponse.of(project, project.get, scheduleService.getSchedule()));
        }
        return pinProjectResponses;
    }

    @Transactional
    public List<ProjectThumbnailResponse> searchProjects(Long memberId, String keyword) {
        return projectRepository.findProjectByMemberIdAndTitleLikeKeyword(memberId, keyword)
                .stream().map(p -> ProjectThumbnailResponse.of(p, ((p.getCover() == null) ? null : p.getCover().getCoverImageUrl()))).toList();
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findMembers(Long projectId, UserDetails userDetails) {
        validateMemberIsInProject(projectId, userDetails);
        return memberProjectRepository.findByProjectIdWithMember(projectId).stream()
                .map(memberProject -> {
                    Member member = Objects.requireNonNull(memberProject.getMember());
                    return MemberResponse.of(member, memberProject.getNickname());
                }).toList();
    }

    private void validateMemberIsInProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        boolean isExist = memberProjectRepository.existsByMemberIdAndProjectId(memberId, projectId);
        if (!isExist) {
            throw new NotEnoughPrivilegeException("속하지 않은 프로젝트 정보를 조회할 수 없습니다.");
        }
    }

    public void createProject(ProjectCreateUpdateRequest request, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));
        Cover cover = coverRepository.findByCoverImageUrl(request.getCoverImageUrl())
                .orElse(null);
        Project project = projectRepository.save(Project.of(request, cover));
        MemberProject memberProject = MemberProject.of(member, project);
        memberProject.grantPrivilege();

        memberProjectRepository.save(memberProject);
    }

    public void deleteProject(Long projectId, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
        projectRepository.delete(project);
    }

    @Transactional
    public void updateProject(Long projectId, ProjectCreateUpdateRequest request, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 프로젝트입니다."));
        Cover cover = coverRepository.findByCoverImageUrl(request.getCoverImageUrl())
                .orElse(null);

        project.updateFrom(request, cover);
    }

    public void kick(Long projectId, Long memberId, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 프로젝트 멤버를 찾을 수 없습니다"));
        if (memberProject.getIsPrivileged()) {
            throw new NotEnoughPrivilegeException("프로젝트 관리자는 강퇴할 수 없습니다");
        }

        memberProjectRepository.delete(memberProject);
    }

    public String generateInviteLink(Long projectId, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        Member member = memberRepository.findById(((CustomUserDetails)userDetails).getId())
                .orElseThrow(() -> new IllegalStateException("요청자의 정보를 찾을 수 없습니다."));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다"));

        String urlData = new StringBuilder().append(projectId)
                .append("?").append(member.getNickname())
                .append("?").append(LocalDateTime.now().plusDays(LinkValidTime.WEEK.value()))
                .toString();

        String url = aesEncoder.encryptAES256(urlData);
        invitationRepository.save(Invitation.of(project, url));
        return url;
    }

    public void addUserToGroup(String url, UserDetails userDetails) {
        // todo:  Inivation 엔티티 존재 확인 + 어떻게 삭제할 것인지?
        StringTokenizer st = new StringTokenizer(aesEncoder.decryptAES256(url), "?");
        Long projectId = Long.valueOf(st.nextToken());

        String invitator = st.nextToken();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        LocalDateTime linkTime = LocalDateTime.parse(st.nextToken(), dateTimeFormatter);
        if (linkTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("초대링크의 유효날짜가 지났습니다.");
        }

        Long memberId = ((CustomUserDetails) userDetails).getId();
        validateJoinIsNotDuplicate(memberId, projectId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다"));

        MemberProject memberProject = MemberProject.of(member, project);
        memberProjectRepository.save(memberProject);
    }

    private void validateJoinIsNotDuplicate(Long memberId, Long projectId) {
        boolean isExist = memberProjectRepository.existsByMemberIdAndProjectId(memberId, projectId);
        if (isExist) {
            // todo: Exception 변경 및 예외처리 핸들러
            throw new IllegalStateException("이미 가입된 사용자입니다");
        }
    }

    private void validateRequesterIsPrivileged(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("멤버-프로젝트 쌍을 찾을 수 없습니다."));
        if (memberProject.getIsPrivileged()) return;
        throw new NotEnoughPrivilegeException("프로젝트 관리자 권한이 없습니다.");
    }

    @Transactional
    public void pinProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("멤버-프로젝트 쌍을 찾을 수 없습니다."));
        memberProject.switchIsPinned();
    }

    public void goOut(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 프로젝트 멤버를 찾을 수 없습니다"));
        if (memberProject.getIsPrivileged()) {
            throw new NotEnoughPrivilegeException("관리자는 나갈 수 없습니다");
        }
        memberProjectRepository.delete(memberProject);
    }

    public void transferPrivilege(Long projectId, TransferPrivilegeRequest request, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        Long fromMemberId = ((CustomUserDetails) userDetails).getId();
        Long toMemberId = request.getToMemberId();

        MemberProject fromMemberProject = memberProjectRepository.findByMemberIdAndProjectId(fromMemberId, projectId)
                .orElseThrow(() -> new IllegalStateException("멤버-프로젝트 쌍을 찾을 수 없습니다."));
        MemberProject toMemberProject = memberProjectRepository.findByMemberIdAndProjectId(toMemberId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("멤버-프로젝트 쌍을 찾을 수 없습니다."));

        fromMemberProject.releasePrivilege();
        toMemberProject.grantPrivilege();

        memberProjectRepository.save(fromMemberProject);
        memberProjectRepository.save(toMemberProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectThumbnailResponse> findStoredProjects(UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        return projectRepository.findAllByMemberIdWhereIsStored(memberId)
                .stream().map(p ->
                        ProjectThumbnailResponse.of(p, ((p.getCover() == null) ? null : p.getCover().getCoverImageUrl()))).toList();
    }
}