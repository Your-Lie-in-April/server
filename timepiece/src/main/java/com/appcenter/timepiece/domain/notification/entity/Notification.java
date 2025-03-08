package com.appcenter.timepiece.domain.notification.entity;

import com.appcenter.timepiece.global.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "sender_id")
    private Long senderId;

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
    private Notification(String message, Long projectId, Long receiverId, Long senderId, NotificationType type) {
        this.message = message;
        this.projectId = projectId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.type = type;
        this.isChecked = false;
        this.isDeleted = false;
    }

    public static Notification of(String message, Long projectId, Long receiverId, Long senderId,
                                  NotificationType type) {
        return Notification.builder()
                .message(message)
                .projectId(projectId)
                .receiverId(receiverId)
                .senderId(senderId)
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
