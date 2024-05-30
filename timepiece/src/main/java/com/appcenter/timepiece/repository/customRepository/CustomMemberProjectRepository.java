package com.appcenter.timepiece.repository.customRepository;

import com.appcenter.timepiece.domain.MemberProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomMemberProjectRepository {
    Page<MemberProject> findMemberProjectsWithProjectAndCover(Pageable pageable, Long memberId, Boolean isDeleted);

    List<MemberProject> findMemberProject(Long memberId, Long projectId, Boolean isPinned, Boolean isStored, Boolean isDeleted);

    Optional<MemberProject> findMemberProjectByMemberIdAndProjectId(Long memberId, Long projectId, Boolean isDeleted);

    Boolean existsMemberProjectByMemberIdAndProjectId(Long memberId, Long projectId, Boolean isDeleted);
}
