package com.appcenter.timepiece.dto.project;

import com.appcenter.timepiece.domain.Project;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class InvitationResponse {
    String title;
    String invitator;
    Boolean isExpired;

    @Builder(access = AccessLevel.PUBLIC)
    private InvitationResponse(String title, String invitator, Boolean isExpired) {
        this.title = title;
        this.invitator = invitator;
        this.isExpired = isExpired;
    }

    public static InvitationResponse of(Project project, String invitator, LocalDateTime linkTime) {
        return new InvitationResponse(project.getTitle(), invitator, linkTime.isBefore(LocalDateTime.now()));
    }
}
