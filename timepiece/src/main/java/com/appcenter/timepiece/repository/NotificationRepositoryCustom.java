package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Notification;
import com.appcenter.timepiece.dto.notify.NotificationResponse;
import java.time.LocalDateTime;
import java.util.List;


public interface NotificationRepositoryCustom {
    List<Notification> findAllByTimestampAfter(Long receiverId, LocalDateTime timestamp, Boolean isChecked,
                                               int pageSize);

    List<Notification> findAllByTimestampAfter(Long receiverId, Long projectId, LocalDateTime timestamp,
                                               Boolean isChecked, int pageSize);

    List<Notification> findAllByReceiverLargerThanNotificationId(Long receiverId, Long notificationId);

    List<Notification> findAllByReceiverLargerThanNotificationId(Long receiverId, Long projectId, Long notificationId);

    List<NotificationResponse> finaAllByReceiverId(Long receiverId, LocalDateTime cursor, Boolean isChecked, int size);

    List<NotificationResponse> finaAllByReceiverIdInProject(Long receiverId, Long projectId, LocalDateTime cursor,
                                                            Boolean isChecked, int size);
}
