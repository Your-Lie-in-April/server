package com.appcenter.timepiece.domain.notification.entity;

import com.appcenter.timepiece.domain.project.entity.MemberProject;
import java.util.List;
import java.util.Objects;

public class NotifyProjectCollection {
    private List<MemberProject> projectMembers;

    public NotifyProjectCollection(List<MemberProject> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public List<MemberProject> excludeSender(Long senderId) {
        return this.projectMembers.stream()
                .filter(memberProject -> !Objects.equals(memberProject.getMemberId(), senderId)).toList();
    }
}
