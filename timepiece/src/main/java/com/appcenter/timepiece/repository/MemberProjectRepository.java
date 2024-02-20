package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.MemberProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberProjectRepository extends JpaRepository<MemberProject, Long> {

    List<MemberProject> findByMemberId(Long memberId);

    List<MemberProject> findByMemberIdAndIsPinnedIsTrue(Long memberId);
}
