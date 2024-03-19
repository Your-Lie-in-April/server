package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.*;
import com.appcenter.timepiece.dto.member.MemberResponse;
import com.appcenter.timepiece.dto.project.PinProjectResponse;
import com.appcenter.timepiece.dto.project.ProjectCreateUpdateRequest;
import com.appcenter.timepiece.dto.project.ProjectResponse;
import com.appcenter.timepiece.dto.project.ProjectThumbnailResponse;
import com.appcenter.timepiece.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final MemberProjectRepository memberProjectRepository;
    private final CoverRepository coverRepository;
    private final ScheduleRepository scheduleRepository;

    public List<ProjectResponse> findAll() {
        return projectRepository.findAllWithCover().stream().map(p ->
                ProjectResponse.of(p, p.getCover().getCoverImageUrl())).toList();
    }

    /**
     *
     * @param memberId 자동생성되는 멤버 식별자(PK)
     * @return 메인페이지에 나타나는 프로젝트 썸네일 정보를 담은 dto 리스트를 리턴합니다.
     */
    public List<ProjectThumbnailResponse> findProjects(Long memberId) {
        return memberProjectRepository.findByMemberId(memberId).stream().map(MemberProject::getProject)
                .map(p -> ProjectThumbnailResponse.of(p, p.getCover().getCoverImageUrl())).toList();
    }

    @Transactional
    public List<PinProjectResponse> findPinProjects(Long memberId) {
        List<MemberProject> memberProjects = memberProjectRepository.findByMemberIdAndIsPinnedIsTrue(memberId);
        List<PinProjectResponse> pinProjectResponses = new ArrayList<>();

        for (MemberProject memberProject: memberProjects) {
            Project project = memberProject.getProject();
            // todo: List<ScheduleWeekResponse>를 생성하는 로직 작성
//            pinProjectResponses.add(PinProjectResponse.of(project, project.get, scheduleService.getSchedule()));
        }
        return pinProjectResponses;
    }

    // todo: Q.반환 타입이 ProjectResponse가 아닌 썸네일이 되야하지 않을까?
    public List<ProjectThumbnailResponse> searchProjects(Long memberId, String keyword) {
        return projectRepository.findProjectByMemberIdAndTitleLikeKeyword(memberId, keyword)
                .stream().map(p -> ProjectThumbnailResponse.of(p, p.getCover().getCoverImageUrl())).toList();
    }

    public List<MemberResponse> findMembers(Long projectId) {
        return memberProjectRepository.findByProjectId(projectId).stream().map(MemberProject::getMember)
                .map(MemberResponse::from).toList();
    }

    public void createProject(ProjectCreateUpdateRequest request, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));
        Cover cover = coverRepository.findById(request.getCoverId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커버이미지입니다."));

        Project project = projectRepository.save(Project.of(request, cover));
        MemberProject memberProject = MemberProject.of(member, project);
        memberProject.grantPrivilege();

        memberProjectRepository.save(memberProject);
    }

    public void deleteProject(Long projectId, UserDetails userDetails) {
        isRequesterPrivileged(projectId, userDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다."));
        projectRepository.delete(project);
    }

    @Transactional
    public void updateProject(Long projectId, ProjectCreateUpdateRequest request, UserDetails userDetails) {
        isRequesterPrivileged(projectId, userDetails);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 프로젝트입니다."));
        Cover cover = coverRepository.findById(request.getCoverId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 커버이미지입니다."));

        project.updateFrom(request, cover);
    }

    public void kick(Long projectId, Long memberId, UserDetails userDetails) {
        isRequesterPrivileged(projectId, userDetails);

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 프로젝트 멤버를 찾을 수 없습니다"));

        memberProjectRepository.delete(memberProject);
    }

    public Map<String, String> generateInviteLink(Long projectId, UserDetails userDetails) {
        isRequesterPrivileged(projectId, userDetails);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다"));

        // todo: project 별 고유 정보를 통해 링크생성(인코딩)
        String url = String.valueOf(projectId);

        Map<String, String> urlData = new HashMap<>();
        urlData.put("url", url);
        return urlData;
    }

    public void addUserToGroup(String url, UserDetails userDetails) {
        // todo: url 정보를 통해 초대받은 프로젝트 정보를 파싱해서 꺼내옴
        Long projectId = Long.valueOf(url);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 프로젝트입니다"));
        Long memberId = ((CustomUserDetails) userDetails).getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다"));

        MemberProject memberProject = MemberProject.of(member, project);
        memberProjectRepository.save(memberProject);
    }

    private void isRequesterPrivileged(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("멤버-프로젝트 쌍을 찾을 수 없습니다."));
        if (memberProject.getIsPrivileged()) return;
        throw new NotEnoughPrivilegeException("현재 사용자는 프로젝트 오너가 아닙니다.");
    }
}