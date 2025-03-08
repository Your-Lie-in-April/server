package com.appcenter.timepiece.domain.project.repository;

import com.appcenter.timepiece.domain.project.dto.ProjectWithCoverDTO;
import com.appcenter.timepiece.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {

    Page<ProjectWithCoverDTO> searchProjectsWithCover(Long memberId, String keyword, Boolean isStored,
                                                      Pageable pageable);

    Page<Project> findProjectIsStored(Long memberId, Pageable pageable);

}
