package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.dto.project.ProjectThumbnailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberProjectRepository extends JpaRepository<MemberProject, Long>, MemberProjectRepositoryCustom {

    @Query("SELECT new com.appcenter.timepiece.dto.project.ProjectThumbnailResponse(p, c.thumbnailUrl) " +
            "FROM Project p " +
            "INNER JOIN MemberProject mp ON mp.projectId = p.id " +
            "LEFT JOIN Cover c ON p.coverId = c.id " +
            "WHERE mp.memberId = :memberId " +
            "AND p.isDeleted = false AND mp.isStored = false " +
            "ORDER BY p.updatedAt DESC")
    Page<ProjectThumbnailResponse> fetchProjectThumbnailResponse(Pageable pageable, Long memberId);
}
