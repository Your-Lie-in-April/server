package com.appcenter.timepiece.dto.member;

import lombok.Getter;

@Getter
public class MemberResponse {

    private Long memberId;
    private String email;
    private String nickname;
    private String state;
    private String profileImageUrl;

    public MemberResponse(Long memberId, String email, String nickname, String state, String profileImageUrl) {
        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.state = state;
        this.profileImageUrl = profileImageUrl;
    }
}
