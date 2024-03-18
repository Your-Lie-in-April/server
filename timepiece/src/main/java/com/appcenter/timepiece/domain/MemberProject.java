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
public class MemberProject extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "memberProject", cascade = CascadeType.ALL)
    private List<Schedule> schedules = new ArrayList<>();

    private String nickname;

    @Column(name = "is_pinned")
    private Boolean isPinned;

    @Column(name = "is_stored")
    private Boolean isStored;

    @Column(name = "is_privileged")
    private Boolean isPrivileged;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberProject(Member member, Project project, String nickname) {
        this.member = member;
        this.project = project;
        this.nickname = nickname;
        this.isPinned = false;
        this.isPrivileged = false;
        this.isStored = false;
    }

    public static MemberProject of(Member member, Project project) {
        return MemberProject.builder()
                .member(member)
                .project(project)
                .nickname(member.getNickname())
                .build();
    }

    public void grantPrivilege() {
        this.isPrivileged = true;
    }

    public void releasePrivilege() {
        this.isPrivileged = false;
    }

    public void switchIsPinned() {
        this.isPinned ^= true;
    }

    public void switchIsStored() {
        this.isStored ^= true;
    }
}
