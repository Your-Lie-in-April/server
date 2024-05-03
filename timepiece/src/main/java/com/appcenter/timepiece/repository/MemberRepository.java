package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByOauth2Id(String oauth2Id);
}
