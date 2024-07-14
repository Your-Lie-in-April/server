package com.appcenter.timepiece.repository;

import com.appcenter.timepiece.domain.Notification;
import com.appcenter.timepiece.domain.QMember;
import com.appcenter.timepiece.domain.QNotification;
import com.appcenter.timepiece.domain.QProject;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private QNotification notification = QNotification.notification;
    private QMember receiver = new QMember("receiver");
    private QMember sender = new QMember("sender");
    private QProject project = QProject.project;

    @Override
    public List<Notification> findAllByTimestampAfter(Long receiverId, LocalDateTime cursorTimestamp, Boolean isChecked, int pageSize) {
        return queryFactory.selectFrom(notification)
                .leftJoin(notification.receiver, receiver)
                .leftJoin(notification.sender, sender)
                .where(notification.receiver.id.eq(receiverId)
                        .and(notification.isChecked.eq(isChecked))
                        .and(notification.isDeleted.isFalse())
                        .and(notification.createdAt.lt(cursorTimestamp)))
                .orderBy(notification.isChecked.asc(), notification.createdAt.desc())
                .limit(pageSize)
                .fetch();
    }


    @Override
    public List<Notification> findAllByTimestampAfter(Long receiverId, Long projectId, LocalDateTime timestamp, Boolean isChecked, int pageSize) {
        return queryFactory.selectFrom(notification)
                .leftJoin(notification.receiver, receiver)
                .leftJoin(notification.sender, sender)
                .leftJoin(notification.project, project)
                .where(notification.receiver.id.eq(receiverId)
                        .and(notification.project.id.eq(projectId))
                        .and(notification.isChecked.eq(isChecked))
                        .and(notification.isDeleted.isFalse())
                        .and(notification.createdAt.lt(timestamp)))
                .orderBy(notification.isChecked.asc(), notification.createdAt.desc())
                .limit(pageSize)
                .fetch();
    }

}
