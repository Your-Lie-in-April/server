package com.appcenter.timepiece.domain.member.service;

import com.appcenter.timepiece.domain.member.dto.MemberResponse;
import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.domain.member.repository.MemberRepository;
import com.appcenter.timepiece.domain.project.entity.MemberProject;
import com.appcenter.timepiece.domain.project.repository.MemberProjectRepository;
import com.appcenter.timepiece.global.exception.ExceptionMessage;
import com.appcenter.timepiece.global.exception.NotFoundElementException;
import com.appcenter.timepiece.global.security.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberProjectRepository memberProjectRepository;

    public List<MemberResponse> getAllMember() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(MemberResponse::from).toList();
    }

    public MemberResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        return MemberResponse.from(member);
    }

    @Transactional
    public void storeProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        memberProject.switchIsStored();

        memberProjectRepository.save(memberProject);
    }

    @Transactional
    public Boolean storeProject2(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        memberProject.switchIsStored();

        memberProjectRepository.save(memberProject);

        return memberProject.getIsStored();
    }

    public void editMemberState(String state, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        member.editState(state);
        memberRepository.save(member);
    }

    @Transactional
    public MemberResponse editMemberNickname(Long projectId, String nickName, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        memberProject.editNickName(nickName);
        memberProjectRepository.save(memberProject);

        return MemberResponse.of(member, memberProject);
    }

}
