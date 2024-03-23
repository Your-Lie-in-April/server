package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.*;
import com.appcenter.timepiece.dto.member.MemberResponse;
import com.appcenter.timepiece.dto.project.*;
import com.appcenter.timepiece.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final MemberProjectRepository memberProjectRepository;
    private final CoverRepository coverRepository;
    private final InvitationRepository invitationRepository;

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

    public List<MemberResponse> findMembers(Long projectId) {
        return memberRepository.findByProjectIdWithMember(projectId).stream().map(MemberResponse::from).toList();
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

    public Map<String, String> generateInviteLink(Long projectId, UserDetails userDetails) {
        validateRequesterIsPrivileged(projectId, userDetails);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다"));

        // todo: project 별 고유 정보를 통해 링크생성(인코딩)
        String url = String.valueOf(projectId);

        Map<String, String> urlData = new HashMap<>();
        urlData.put("url", url);

        invitationRepository.save(Invitation.of(project, url));
        return urlData;
    }

    public void addUserToGroup(String url, UserDetails userDetails) {
        // todo: url 정보를 통해 초대받은 프로젝트 정보를 파싱해서 꺼내옴 + Inivation 엔티티 존재 확인
        Long projectId = Long.valueOf(url);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다"));
        Long memberId = ((CustomUserDetails) userDetails).getId();

        validateJoinIsNotDuplicate(project, memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다"));

        MemberProject memberProject = MemberProject.of(member, project);
        memberProjectRepository.save(memberProject);
    }

    private void validateJoinIsNotDuplicate(Project project, Long memberId) {
        boolean isExist = memberProjectRepository.existsByMemberIdAndProjectId(memberId, project.getId());
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
        Long myId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(myId, projectId)
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
}