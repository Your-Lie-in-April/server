package com.appcenter.timepiece.dto.member;

import com.appcenter.timepiece.domain.MemberProject;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MemberInfoResponse {

    private Long id;

    private String provider;

    private String nickname;

    private String email;

    private String state;

    private String profileImageUrl;

    private List<String> role;

    private List<MemberProject> memberProjects = new ArrayList<>();

    @Builder
    public MemberInfoResponse(Long id, String provider, String nickname, String email, String state, String profileImageUrl, List<String> role) {
        this.id = id;
        this.provider = provider;
        this.nickname = nickname;
        this.email = email;
        this.state = state;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }
}
