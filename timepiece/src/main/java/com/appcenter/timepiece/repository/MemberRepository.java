package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Member getByEmail(String email);

    Member findMemberById(Long Id);


}
