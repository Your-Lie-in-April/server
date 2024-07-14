package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom{
}
