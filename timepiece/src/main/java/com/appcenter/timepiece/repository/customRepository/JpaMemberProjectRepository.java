package com.appcenter.timepiece.repository.customRepository;

import com.appcenter.timepiece.domain.MemberProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMemberProjectRepository extends JpaRepository<MemberProject, Long> {

}
