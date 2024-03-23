package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.MemberProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberProjectRepository extends JpaRepository<MemberProject, Long> {

    @Query("select mp from MemberProject mp " +
            "join fetch mp.project p " +
            "left join fetch p.cover c " +
            "where mp.member.id = :memberId")
    List<MemberProject> findMemberProjectsWithProjectAndCover(Long memberId);

    @Query("select mp from MemberProject mp " +
            "join mp.project p " +
            "where p.id = :projectId")
    List<MemberProject> findByProjectIdWithMember(Long projectId);

    List<MemberProject> findByMemberIdAndIsPinnedIsTrue(Long memberId);

    Optional<MemberProject> findByMemberIdAndProjectId(Long memberId, Long projectId);

    boolean existsByMemberIdAndProjectId(Long memberId, Long projectId);

}
