package com.appcenter.timepiece.notify;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReceiverId(Pageable pageable, Long receiverId);
    Page<Notification> findByReceiverIdAndProjectId(Pageable pageable, Long receiverId, Long projectId);
}
