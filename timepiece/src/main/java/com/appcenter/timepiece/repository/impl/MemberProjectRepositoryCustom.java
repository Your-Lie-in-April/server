package com.appcenter.timepiece.repository.impl;

import com.appcenter.timepiece.domain.MemberProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberProjectRepositoryCustom {

    Page<MemberProject> findMemberProjectsWithProjectAndCover(Pageable pageable, Long memberId);

    List<MemberProject> findByMemberIdAndIsPinnedIsTrue(Long memberId);

    List<MemberProject> findAllByProjectId(Long projectId);

    boolean existsByMemberIdAndProjectId(Long memberId, Long projectId);

    Optional<MemberProject> findByMemberIdAndProjectId(Long memberId, Long projectId);

}
