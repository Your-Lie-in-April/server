package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Notification;
import com.appcenter.timepiece.domain.QMemberProject;
import com.appcenter.timepiece.domain.QNotification;
import com.appcenter.timepiece.domain.QProject;
import com.appcenter.timepiece.dto.notify.NotificationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private QNotification notification = QNotification.notification;
    private QMemberProject receiver = new QMemberProject("receiver");
    private QMemberProject sender = new QMemberProject("sender");
    private QProject project = QProject.project;

    @Override
    public List<Notification> findAllByTimestampAfter(Long receiverId, LocalDateTime cursorTimestamp, Boolean isChecked,
                                                      int pageSize) {
        return queryFactory.selectFrom(notification)
                .where(notification.receiverId.eq(receiverId)
                        .and(notification.isChecked.eq(isChecked))
                        .and(notification.isDeleted.isFalse())
                        .and(notification.createdAt.lt(cursorTimestamp)))
                .orderBy(notification.isChecked.asc(), notification.createdAt.desc())
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Notification> findAllByTimestampAfter(Long receiverId, Long projectId, LocalDateTime timestamp,
                                                      Boolean isChecked, int pageSize) {
        return queryFactory.selectFrom(notification)
                .where(notification.receiverId.eq(receiverId)
                        .and(notification.projectId.eq(projectId))
                        .and(notification.isChecked.eq(isChecked))
                        .and(notification.isDeleted.isFalse())
                        .and(notification.createdAt.lt(timestamp)))
                .orderBy(notification.isChecked.asc(), notification.createdAt.desc())
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<Notification> findAllByReceiverLargerThanNotificationId(Long receiverId, Long notificationId) {
        return queryFactory.selectFrom(notification)
                .where(notification.receiverId.eq(receiverId)
                        .and(notification.id.gt(notificationId)))
                .orderBy(notification.isChecked.asc(), notification.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Notification> findAllByReceiverLargerThanNotificationId(Long receiverId, Long projectId,
                                                                        Long notificationId) {
        return queryFactory.selectFrom(notification)
                .where(notification.receiverId.eq(receiverId)
                        .and(notification.projectId.eq(projectId))
                        .and(notification.id.gt(notificationId)))
                .orderBy(notification.isChecked.asc(), notification.createdAt.desc())
                .fetch();
    }

    @Override
    public List<NotificationResponse> finaAllByReceiverId(Long receiverId, LocalDateTime cursor, Boolean isChecked,
                                                          int size) {
        return queryFactory
                .select(Projections.constructor(NotificationResponse.class, notification, project, sender, receiver))
                .from(notification)
                .leftJoin(sender).on(notification.senderId.eq(sender.id))
                .leftJoin(receiver).on(notification.receiverId.eq(receiver.id))
                .leftJoin(project).on(notification.projectId.eq(project.id))
                .where(notification.receiverId.eq(receiverId)
                        .and(notification.isChecked.eq(isChecked))
                        .and(notification.isDeleted.isFalse())
                        .and(notification.createdAt.lt(cursor)))
                .orderBy(notification.isChecked.asc(), notification.createdAt.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<NotificationResponse> finaAllByReceiverIdInProject(Long receiverId, Long projectId,
                                                                   LocalDateTime cursor, Boolean isChecked, int size) {
        return queryFactory
                .select(Projections.constructor(NotificationResponse.class, notification, project, sender, receiver))
                .from(notification)
                .leftJoin(sender).on(notification.senderId.eq(sender.id))
                .leftJoin(receiver).on(notification.receiverId.eq(receiver.id))
                .leftJoin(project).on(notification.projectId.eq(project.id))
                .where(notification.receiverId.eq(receiverId)
                        .and(notification.projectId.eq(projectId))
                        .and(notification.isChecked.eq(isChecked))
                        .and(notification.isDeleted.isFalse())
                        .and(notification.createdAt.lt(cursor)))
                .orderBy(notification.isChecked.asc(), notification.createdAt.desc())
                .limit(size)
                .fetch();
    }
}
