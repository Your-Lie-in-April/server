package com.appcenter.timepiece.service;

import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.domain.MemberProject;

import java.util.List;
import java.util.Objects;

public class NotifyProjectCollection {
    private List<MemberProject> projectMembers;

    public NotifyProjectCollection(List<MemberProject> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public List<MemberProject> excludeSender(Member sender) {
        return this.projectMembers.stream()
                .filter(memberProject -> !Objects.equals(memberProject.getMember().getId(), sender.getId())).toList();
    }
}
