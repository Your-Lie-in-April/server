package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from MemberProject mp " +
            "join mp.project p " +
            "join mp.member m " +
            "where p.id = :projectId")
    List<Member> findByProjectIdWithMember(Long projectId);

    Optional<Member> findByEmail(String email);

    Member getByEmail(String email);


}
