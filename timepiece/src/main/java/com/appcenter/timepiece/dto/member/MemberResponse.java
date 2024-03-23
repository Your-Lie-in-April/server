package com.appcenter.timepiece.dto.member;

import com.appcenter.timepiece.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private Long memberId;

    private String email;

    private String nickname;

    private String state;

    private String profileImageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberResponse(Long memberId, String email, String nickname, String state, String profileImageUrl) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.state = state;
        this.profileImageUrl = profileImageUrl;
    }

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .state(member.getState())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }

    public static MemberResponse of(Member member, String nickname) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(nickname)
                .state(member.getState())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
