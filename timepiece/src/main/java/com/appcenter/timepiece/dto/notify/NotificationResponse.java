package com.appcenter.timepiece.dto.notify;

import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Notification;
import com.appcenter.timepiece.domain.Project;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NotificationResponse {

    private Long notificationId;

    private String message;

    private ProjectInfo project;

    private MemberInfo receiver;

    private MemberInfo sender;

    private Notification.NotificationType type;

    private Boolean isChecked;

    private LocalDateTime createdAt;

    @Getter
    public static class ProjectInfo {
        private Long projectId;

        private String title;

        public ProjectInfo(Long projectId, String title) {
            this.projectId = projectId;
            this.title = title;
        }

        public static ProjectInfo from(Project project) {
            if (project == null) {
                return null;
            }
            return new ProjectInfo(project.getId(), project.getTitle());
        }
    }

    @Getter
    public static class MemberInfo {
        private Long memberId;

        private String nickname;

        public MemberInfo(Long memberId, String nickname) {
            this.memberId = memberId;
            this.nickname = nickname;
        }

        public static MemberInfo fromMemberProject(MemberProject memberProject) {
            if (memberProject == null) {
                return null;
            }
            return new MemberInfo(memberProject.getMemberId(), memberProject.getNickname());
        }

        public static MemberInfo fromMember(Member member) {
            if (member == null) {
                return null;
            }
            return new MemberInfo(member.getId(), member.getNickname());
        }
    }

    @JsonCreator
    @Builder(access = AccessLevel.PRIVATE)
    public NotificationResponse(Long notificationId, String message, ProjectInfo project, MemberInfo receiver,
                                MemberInfo sender, Notification.NotificationType type, Boolean isChecked,
                                LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.message = message;
        this.project = project;
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.isChecked = isChecked;
        this.createdAt = createdAt;
    }

    public NotificationResponse(Notification notification, Project project, MemberProject sender,
                                MemberProject receiver
    ) {
        this.notificationId = notification.getId();
        this.message = notification.getMessage();
        this.project = ProjectInfo.from(project);
        this.receiver = MemberInfo.fromMemberProject(receiver);
        this.sender = MemberInfo.fromMemberProject(sender);
        this.type = notification.getType();
        this.isChecked = notification.getIsChecked();
        this.createdAt = notification.getCreatedAt();
    }


    public static NotificationResponse from(Notification notification, Project project, MemberProject sender,
                                            MemberProject receiver
    ) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .message(notification.getMessage())
                .project(ProjectInfo.from(project))
                .receiver(MemberInfo.fromMemberProject(receiver))
                .sender(MemberInfo.fromMemberProject(sender))
                .type(notification.getType())
                .isChecked(notification.getIsChecked())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
