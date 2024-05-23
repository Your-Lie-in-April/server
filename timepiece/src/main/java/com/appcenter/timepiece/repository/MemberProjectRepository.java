package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.MemberProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberProjectRepository extends JpaRepository<MemberProject, Long> {

    //소속 프로젝트 전체 조회
    @Query("select mp from MemberProject mp " +
            "join fetch mp.project p " +
            "left join fetch p.cover c " +
            "where mp.member.id = :memberId " +
            "and mp.isStored=false " +
            "and p.isDeleted=false")
    Page<MemberProject> findMemberProjectsWithProjectAndCover(Pageable pageable, Long memberId);

    //프로젝트에 속해있는 유저 전체 조회
    @Query("select mp from MemberProject mp " +
            "join fetch mp.project p " +
            "where p.id = :projectId and p.isDeleted=false")
    List<MemberProject> findByProjectIdWithMember(Long projectId);

    //핀 프로젝트 조회
    @Query("select mp from MemberProject mp " +
            "where mp.member.id = :memberId " +
            "And mp.isPinned = true " +
            "And mp.project.isDeleted=false")
    List<MemberProject> findByMemberIdAndIsPinnedIsTrue(Long memberId);

    @Query("select mp from MemberProject mp " +
            "where mp.member.id = :memberId " +
            "And mp.project.id = :projectId " +
            "And mp.project.isDeleted = false")
    Optional<MemberProject> findByMemberIdAndProjectId(Long memberId, Long projectId);

    @Query("select mp from MemberProject mp " +
            "where mp.project.id = :projectId " +
            "And mp.project.isDeleted = false")
    List<MemberProject> findAllByProjectId(Long projectId);

    boolean existsByMemberIdAndProjectIdAndProjectIsDeletedIsFalse(Long memberId, Long projectId);

}
