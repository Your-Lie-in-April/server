package com.appcenter.timepiece.domain.project.repository;

import com.appcenter.timepiece.domain.project.entity.Cover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverRepository extends JpaRepository<Cover, Long> {

}
