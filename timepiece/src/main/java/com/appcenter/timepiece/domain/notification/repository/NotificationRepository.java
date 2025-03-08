package com.appcenter.timepiece.domain.notification.repository;

import com.appcenter.timepiece.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
}
