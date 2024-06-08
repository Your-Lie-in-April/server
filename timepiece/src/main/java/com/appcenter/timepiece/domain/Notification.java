package com.appcenter.timepiece.domain;

import com.appcenter.timepiece.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private Boolean isChecked;

    @Column(nullable = false)
    private Boolean isDeleted;

    public enum NotificationType {
        SYSTEM, SIGN, SCHEDULE;
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Notification(String message, Project project, Member receiver, Member sender, NotificationType type) {
        this.message = message;
        this.project = project;
        this.receiver = receiver;
        this.sender = sender;
        this.type = type;
        this.isChecked = false;
        this.isDeleted = false;
    }

    public static Notification of(String message, Project project, Member receiver, Member sender, NotificationType type) {
        return Notification.builder()
                .message(message)
                .project(project)
                .receiver(receiver)
                .sender(sender)
                .type(type)
                .build();
    }

    public void check() {
        isChecked = true;
    }

    public void delete() {
        isDeleted = true;
    }
}
