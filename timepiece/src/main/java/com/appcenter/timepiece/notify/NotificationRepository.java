package com.appcenter.timepiece.notify;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverId(Long receiverId);
    List<Notification> findByReceiverIdAndProjectId(Long receiverId, Long projectId);
}
