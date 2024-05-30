package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.NotFoundElementException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.dto.member.MemberResponse;
import com.appcenter.timepiece.repository.MemberProjectRepository;
import com.appcenter.timepiece.repository.MemberRepository;
import com.appcenter.timepiece.repository.customRepository.CustomMemberProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberProjectRepository memberProjectRepository;

    private final CustomMemberProjectRepository customMemberProjectRepository;

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

    public void storeProject(Long projectId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        MemberProject memberProject = customMemberProjectRepository.findMemberProjectByMemberIdAndProjectId(memberId, projectId, false)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        memberProject.switchIsStored();

        memberProjectRepository.save(memberProject);
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

        MemberProject memberProject = customMemberProjectRepository.findMemberProjectByMemberIdAndProjectId(memberId, projectId, false)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        memberProject.editNickName(nickName);
        memberProjectRepository.save(memberProject);
        return MemberResponse.of(memberProject.getMember(), memberProject);
    }

}
