package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Project;
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
            "where m.id = :memberId and p.title like %:keyword%")
    List<Project> findProjectByMemberIdAndTitleLikeKeyword(Long memberId, String keyword);
}
