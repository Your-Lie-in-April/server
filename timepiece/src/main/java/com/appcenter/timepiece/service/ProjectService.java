package com.appcenter.timepiece.service;

import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Project;
import com.appcenter.timepiece.dto.member.MemberResponse;
import com.appcenter.timepiece.dto.project.PinProjectResponse;
import com.appcenter.timepiece.dto.project.ProjectResponse;
import com.appcenter.timepiece.dto.project.ProjectThumbnailResponse;
import com.appcenter.timepiece.repository.MemberProjectRepository;
import com.appcenter.timepiece.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberProjectRepository memberProjectRepository;

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
        List<MemberProject> memberProject = memberProjectRepository.findByMemberIdAndIsPinnedIsTrue(memberId);
        List<PinProjectResponse> pinProjectResponses = new ArrayList<>();

        for (MemberProject mp: memberProject) {
            Project project = mp.getProject();
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
}
