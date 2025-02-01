package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.MemberProject;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberProjectRepository extends JpaRepository<MemberProject, Long>, MemberProjectRepositoryCustom {

    Optional<MemberProject> findByMemberId(Long memberId);
}
