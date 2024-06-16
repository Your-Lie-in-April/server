package com.appcenter.timepiece.repository.customRepository;

import com.appcenter.timepiece.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepositoryCustom {

    Page<Project> searchProject(Long memberId, String keyword, Boolean isStored, Pageable pageable);

    Page<Project> findProjectIsStored(Long memberId, Pageable pageable);

}
