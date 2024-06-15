package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.repository.customRepository.JpaMemberProjectRepository;
import com.appcenter.timepiece.repository.impl.QueryDSLMemberProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberProjectRepository {

    private final JpaMemberProjectRepository jpaMemberProjectRepository;

    private final QueryDSLMemberProjectRepository queryDSLMemberProjectRepository;


    public Page<MemberProject> findMemberProjectsWithProjectAndCover(Pageable pageable, Long memberId) {
        return queryDSLMemberProjectRepository.findMemberProjectsWithProjectAndCover(pageable, memberId, false);
    }

    public List<MemberProject> findByProjectIdWithMember(Long projectId) {
        return queryDSLMemberProjectRepository.findMemberProject(null, projectId, null, null, false);
    }

    public List<MemberProject> findByMemberIdAndIsPinnedIsTrue(Long memberId) {
        return queryDSLMemberProjectRepository.findMemberProject(memberId, null, true, null, false);
    }

    public Optional<MemberProject> findByMemberIdAndProjectId(Long memberId, Long projectId) {
        return queryDSLMemberProjectRepository.findMemberProjectByMemberIdAndProjectId(memberId, projectId, false);
    }

    public List<MemberProject> findAllByProjectId(Long projectId) {
        return queryDSLMemberProjectRepository.findMemberProject(null, projectId, null, null, false);
    }

    public boolean existsByMemberIdAndProjectId(Long memberId, Long projectId) {
        return queryDSLMemberProjectRepository.existsMemberProjectByMemberIdAndProjectId(memberId, projectId, false);
    }
}
