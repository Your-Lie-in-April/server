package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Cover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoverRepository extends JpaRepository<Cover, Long> {

    Optional<Cover> findById(Long coverId);
}
