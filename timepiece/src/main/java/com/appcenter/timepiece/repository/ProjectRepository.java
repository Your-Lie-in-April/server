package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("select p from Project p left join fetch p.cover c")
    List<Project> findAllWithCover();

    @Query("select distinct p from Project p " +
            "join p.memberProjects mp " +
            "join mp.member m " +
            "where m.id = :memberId and p.title like %:keyword% and mp.isStored = :isStored")
    Page<Project> findProjectByMemberIdAndTitleLikeKeyword(Boolean isStored, Long memberId, String keyword, Pageable pageable);

    @Query("select distinct p from Project p " +
            "join p.memberProjects mp " +
            "where mp.member.id = :memberId and mp.isStored = TRUE ")
    Page<Project> findAllByMemberIdWhereIsStored(Long memberId, Pageable pageable);

}
