package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByReceiverIdAndIsDeletedIsFalse(Pageable pageable, Long receiverId);
    Page<Notification> findByReceiverIdAndProjectIdAndIsDeletedIsFalse(Pageable pageable, Long receiverId, Long projectId);
}
