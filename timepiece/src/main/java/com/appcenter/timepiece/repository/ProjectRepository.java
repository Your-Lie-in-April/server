package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Project;
import com.appcenter.timepiece.repository.customRepository.ProjectRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {

    @Query("select p from Project p left join fetch p.cover c")
    List<Project> findAllWithCover();

}
