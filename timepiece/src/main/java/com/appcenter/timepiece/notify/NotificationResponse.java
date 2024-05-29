package com.appcenter.timepiece.notify;

import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.domain.Project;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
            if (project == null) return null;
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

        public static MemberInfo from(Member member) {
            if (member == null) return null;
            return new MemberInfo(member.getId(), member.getNickname());
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    public NotificationResponse(Long notificationId, String message, ProjectInfo project, MemberInfo receiver, MemberInfo sender, Notification.NotificationType type, Boolean isChecked, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.message = message;
        this.project = project;
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.isChecked = isChecked;
        this.createdAt = createdAt;
    }

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .message(notification.getMessage())
                .project(ProjectInfo.from(notification.getProject()))
                .receiver(MemberInfo.from(notification.getReceiver()))
                .sender(MemberInfo.from(notification.getSender()))
                .type(notification.getType())
                .isChecked(notification.getIsChecked())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
