package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {

}
