package com.appcenter.timepiece.dto.project;

import com.appcenter.timepiece.domain.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InvitationLinkResponse {
    Long projectId;
    String title;
    String link;

    private InvitationLinkResponse(Long projectId, String title, String link) {
        this.projectId = projectId;
        this.title = title;
        this.link = link;
    }

    public static InvitationLinkResponse of(Project project, String link) {
        return new InvitationLinkResponse(project.getId(), project.getTitle(), link);
    }
}
