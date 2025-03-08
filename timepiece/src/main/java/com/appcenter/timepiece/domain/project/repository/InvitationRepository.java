package com.appcenter.timepiece.domain.project.repository;

import com.appcenter.timepiece.domain.project.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
}
