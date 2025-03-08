package com.appcenter.timepiece.domain.member.dto;

import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.domain.project.entity.MemberProject;
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

    private Boolean isPrivileged;

    @Builder(access = AccessLevel.PUBLIC)
    private MemberResponse(Long memberId, String email, String nickname, String state, String profileImageUrl,
                           Boolean isPrivileged) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.state = state;
        this.profileImageUrl = profileImageUrl;
        this.isPrivileged = isPrivileged;
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

    public static MemberResponse of(Member member, MemberProject memberProject) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(memberProject.getNickname())
                .state(member.getState())
                .profileImageUrl(member.getProfileImageUrl())
                .isPrivileged(memberProject.getIsPrivileged())
                .build();
    }

    @Override
    public String toString() {
        return "{" + "memberId = " + memberId + ", email = " + email + ", nickname = " + nickname + ", state = " + state
                + ", profileImageUrl = " + profileImageUrl + ", isPrivileged = " + isPrivileged + "}";
    }
}
