package com.appcenter.timepiece.domain.project.entity;

import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProject extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "project_id")
    private Long projectId;

    private String nickname;

    @Column(name = "is_pinned")
    private Boolean isPinned;

    @Column(name = "is_stored")
    private Boolean isStored;

    @Column(name = "is_privileged")
    private Boolean isPrivileged;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberProject(Long memberId, Long projectId, String nickname) {
        this.memberId = memberId;
        this.projectId = projectId;
        this.nickname = nickname;
        this.isPinned = false;
        this.isPrivileged = false;
        this.isStored = false;
    }

    public static MemberProject of(Member member, Project project) {
        return MemberProject.builder()
                .memberId(member.getId())
                .projectId(project.getId())
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

    public void editNickName(String nickname) {
        this.nickname = nickname;
    }

}
