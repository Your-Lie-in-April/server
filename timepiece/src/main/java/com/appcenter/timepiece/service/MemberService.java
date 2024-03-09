package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.NotFoundMemberException;
import com.appcenter.timepiece.common.security.JwtProvider;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.dto.member.MemberInfoResponse;
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

    private final JwtProvider jwtProvider;
    public List<MemberInfoResponse> getAllMember(){
        log.info("[getAllMember] 모든 유저 조회");
        List<Member> memberList = memberRepository.findAll();

        List<MemberInfoResponse> memberListDto = memberList.stream()
                .map(member -> new MemberInfoResponse(member.getId(), member.getProvider(),member.getNickname() ,member.getEmail(),member.getState(), member.getProfileImageUrl(), member.getRole())).collect(Collectors.toList());

        return memberListDto;
    }

    public String getMemberState(HttpServletRequest request){
        log.info("[getMemberState] 맴버 상태 조회");

        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveToken(request));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new NotFoundMemberException(ExceptionMessage.MEMBER_NOTFOUND));

        return member.getState();
    }

    public void editMemberState(String status, HttpServletRequest request){
        log.info("[editMemberState] 멤버 상태 수정");

        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveToken(request));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new NotFoundMemberException(ExceptionMessage.MEMBER_NOTFOUND));

        member.editStatus(status);
        memberRepository.save(member);
    }

//    public void editMemberNickname(Long projectId, HttpServletRequest request){
//        Long memberId = jwtProvider.getMemberId(jwtProvider.resolveToken(request));
//
//
//    }
}
