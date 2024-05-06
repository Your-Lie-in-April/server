package com.appcenter.timepiece.domain;

import com.appcenter.timepiece.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    private String state;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List<String> role;

    @OneToMany(mappedBy = "member")
    private List<MemberProject> memberProjects = new ArrayList<>();


    @Builder
    public Member(String provider, String nickname, String email, String state, String profileImageUrl, List<String> role) {
        this.provider = provider;
        this.nickname = nickname;
        this.email = email;
        this.state = state;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    public Member updateMember(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;

        return this;
    }

    public void editState(String state) {
        this.state = state;
    }


}
