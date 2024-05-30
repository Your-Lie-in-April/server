package com.appcenter.timepiece.repository.customRepository;

import com.appcenter.timepiece.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomProjectRepository {

    Page<Project> findProject(Long memberId, String keyword, Boolean isStored, Boolean isDeleted, Pageable pageable);

}
