package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.NotFoundMemberException;
import com.appcenter.timepiece.common.security.JwtProvider;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.dto.member.MemberResponse;
import com.appcenter.timepiece.repository.MemberProjectRepository;
import com.appcenter.timepiece.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberProjectRepository memberProjectRepository;

    private final JwtProvider jwtProvider;

    public List<MemberResponse> getAllMember() {
        log.info("[getAllMember] 모든 유저 조회");
        List<Member> memberList = memberRepository.findAll();

        List<MemberResponse> memberListDto = memberList.stream()
                .map(member -> MemberResponse.from(member)).collect(Collectors.toList());

        return memberListDto;
    }

    public MemberResponse getMemberInfo(Long memberId) {
        log.info("[getMemberInfo] 유저의 정보 조회");

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(ExceptionMessage.MEMBER_NOTFOUND));

        return MemberResponse.from(member);
    }

    public void storeProject(Long projectId, HttpServletRequest request) {
        log.info("[storeProject] 프로젝트 보관");
        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveServiceToken(request));

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundMemberException(ExceptionMessage.MEMBER_NOTFOUND));

        memberProject.switchIsStored();

        memberProjectRepository.save(memberProject);
    }

    public void editMemberState(String state, HttpServletRequest request) {
        log.info("[editMemberState] 멤버 상태 수정 state = {}", state);

        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveServiceToken(request));
        log.info("memberId = {}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(ExceptionMessage.MEMBER_NOTFOUND));

        member.editState(state);
        memberRepository.save(member);
    }

    public void editMemberNickname(Long projectId, String nickName, HttpServletRequest request) {
        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveServiceToken(request));

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundMemberException(ExceptionMessage.MEMBER_NOTFOUND));

        memberProject.editNickName(nickName);
        memberProjectRepository.save(memberProject);
    }

    public void deleteStoredProject(Long projectId, HttpServletRequest request) {
        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveServiceToken(request));

        MemberProject memberProject = memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
                .orElseThrow(() -> new NotFoundMemberException(ExceptionMessage.MEMBER_NOTFOUND));

        memberProjectRepository.delete(memberProject);

    }

}
