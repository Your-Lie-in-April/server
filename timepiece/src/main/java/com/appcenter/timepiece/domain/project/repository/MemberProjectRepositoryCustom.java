package com.appcenter.timepiece.domain.project.repository;

import com.appcenter.timepiece.domain.project.entity.MemberProject;
import java.util.List;
import java.util.Optional;

public interface MemberProjectRepositoryCustom {

    List<MemberProject> findByMemberIdAndIsPinnedIsTrue(Long memberId);

    List<MemberProject> findAllByProjectId(Long projectId);

    boolean existsByMemberIdAndProjectId(Long memberId, Long projectId);

    Optional<MemberProject> findByMemberIdAndProjectId(Long memberId, Long projectId);

}
