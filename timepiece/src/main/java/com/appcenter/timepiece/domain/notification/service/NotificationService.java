package com.appcenter.timepiece.domain.notification.service;

import com.appcenter.timepiece.domain.member.entity.Member;
import com.appcenter.timepiece.domain.notification.dto.NotificationResponse;
import com.appcenter.timepiece.domain.notification.entity.Notification;
import com.appcenter.timepiece.domain.notification.entity.NotifyProjectCollection;
import com.appcenter.timepiece.domain.notification.repository.NotificationRepository;
import com.appcenter.timepiece.domain.project.entity.MemberProject;
import com.appcenter.timepiece.domain.project.entity.Project;
import com.appcenter.timepiece.domain.project.repository.MemberProjectRepository;
import com.appcenter.timepiece.domain.project.repository.ProjectRepository;
import com.appcenter.timepiece.global.common.dto.CommonCursorPagingResponse;
import com.appcenter.timepiece.global.exception.ExceptionMessage;
import com.appcenter.timepiece.global.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.global.exception.NotFoundElementException;
import com.appcenter.timepiece.global.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${spring.sse.connection-timeout}")
    private long DEFAULT_TIMEOUT;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final NotificationRepository notificationRepository;
    private final MemberProjectRepository memberProjectRepository;
    private final ProjectRepository projectRepository;


    public SseEmitter subscribe(UserDetails userDetails, Long lastEventId, Long projectId) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(memberId, emitter);

        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(memberId);
            log.info("Emitter timeout, will be removed and client should reconnect");

        });
        emitter.onError((e) -> {
            emitter.completeWithError(e);
            emitters.remove(memberId);
            log.error("Emitter error, removed and client should reconnect", e);

        });
        emitter.onCompletion(() -> this.emitters.remove(memberId));

        try {
            emitter.send("connection created");
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        if (Objects.nonNull(lastEventId)) {
            recoverNotification(memberId, lastEventId, projectId);
        }

        return emitter;
    }

    public void notifyScheduleChanging(Project project, Member sender) {
        String message = project.getTitle() + "의 " + sender.getNickname() + "이 스케줄을 변경했습니다.";
        List<MemberProject> memberProjects = memberProjectRepository.findAllByProjectId(project.getId());

        NotifyProjectCollection notifyProjectCollection = new NotifyProjectCollection(memberProjects);
        notifyProjectCollection.excludeSender(sender.getId()).forEach(memberProject -> {
            sendNotification(message, project.getId(), memberProject.getMemberId(), sender.getId(),
                    Notification.NotificationType.SCHEDULE);
        });
    }

    public void notifySigning(Project project, Member sender) {
        String message = project.getTitle() + "에 " + sender.getNickname() + "가 가입했습니다.";
        List<MemberProject> memberProjects = memberProjectRepository.findAllByProjectId(project.getId());

        NotifyProjectCollection notifyProjectCollection = new NotifyProjectCollection(memberProjects);
        notifyProjectCollection.excludeSender(sender.getId()).forEach(memberProject -> {
            sendNotification(message, project.getId(), memberProject.getMemberId(), sender.getId(),
                    Notification.NotificationType.SIGN);
        });
    }

    public void notifyBecomingOwner(Project project, Long receiverId, Long senderId) {
        String message = project.getTitle() + "의 관리자가 되었습니다.";
        sendNotification(message, project.getId(), receiverId, senderId, Notification.NotificationType.SYSTEM);
    }

    private void sendNotification(String message, Long projectId, Long receiverId, Long senderId,
                                  Notification.NotificationType type) {
        Notification notification = Notification.of(message, projectId, receiverId, senderId, type);
        notification = notificationRepository.save(notification);
        publishNotification(notification);
    }

    // 재연결 시 유실된 메시지를 찾아 재전송
    private void recoverNotification(Long memberId, Long lastNotificationId, Long projectId) {
        List<Notification> notifications;
        if (projectId == null) {
            notifications = notificationRepository.findAllByReceiverLargerThanNotificationId(
                    memberId, lastNotificationId);
        } else {
            notifications = notificationRepository.findAllByReceiverLargerThanNotificationId(
                    memberId, projectId, lastNotificationId);
        }
        notifications.forEach(this::publishNotification);
    }

    protected void publishNotification(Notification notification) {
        Project project = projectRepository.findById(notification.getProjectId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        MemberProject sender = memberProjectRepository.findByMemberIdAndProjectId(notification.getSenderId(),
                        notification.getProjectId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        MemberProject receiver = memberProjectRepository.findByMemberIdAndProjectId(notification.getReceiverId(),
                        notification.getProjectId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));

        NotificationResponse notificationResponse = NotificationResponse.from(notification, project, sender, receiver);
        redisTemplate.convertAndSend("notificationTopic", notificationResponse);
    }

    @Bean
    RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener((message, channel) -> {
            String body = redisTemplate.getStringSerializer().deserialize(message.getBody());
            NotificationResponse notificationResponse = null;

            try {
                objectMapper.findAndRegisterModules();
                notificationResponse = objectMapper.readValue(body, NotificationResponse.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            assert notificationResponse != null;

            consumeNotification(notificationResponse);
        }, new PatternTopic("notificationTopic"));
        return container;
    }

    /*
        SseEmitters에 대한 정보를 메모리상 ConcurrentMap으로 보관.
        -> 다중 WAS 환경에서는 각 서버가 지닌 SseEmitters 정보가 다름
        즉, 현재 찾고있는 Emitter가 현재 서버에 없을수도 있다.
        => Redis pub/sub은 모든 subscriber에게 동일한 메시지를 보낸다.(하나의 컨슈머에게 보내는 카프카와는 다름)
        모든 WAS는 동일한 메시지를 받고 해당되는 Emitter를 가진 서버가 이를 처리한다.
        따라서 emitter가 null이 아닌지 체크하는 로직이 필요하다.
     */
    private void consumeNotification(NotificationResponse notificationResponse) {
        SseEmitter emitter = emitters.get(notificationResponse.getReceiver().getMemberId());
        if (emitter != null) {
            // todo: 스레드 적용 전, 후 성능 측정
            executor.execute(() -> {
                try {
                    emitter.send(SseEmitter.event()
                            .id(notificationResponse.getReceiver().getMemberId().toString())
                            .name("notification")
                            .data(notificationResponse)
                            .reconnectTime(1000L));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                    emitters.remove(notificationResponse.getReceiver().getMemberId());
                    throw new RuntimeException("Failed to send SSE message");
                }
            });
        }
    }


    // todo: paging, sorting, | exclude Checked/Deleted(soft) Notification
    @Transactional
    public CommonCursorPagingResponse<?> getNotifications(LocalDateTime cursor, Boolean isChecked, Integer size,
                                                          UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        List<NotificationResponse> notifications = notificationRepository.finaAllByReceiverId(memberId, cursor,
                isChecked,
                size + 1);

        Boolean hasMore = notifications.size() > size;
        LocalDateTime nextCursor = null;
        if (hasMore) {
            notifications.remove(notifications.size() - 1);
            nextCursor = notifications.get(notifications.size() - 1).getCreatedAt();
        }
        return new CommonCursorPagingResponse<>(size, nextCursor, hasMore, notifications);
    }

    @Transactional
    public CommonCursorPagingResponse<?> getNotificationsInProject(Long projectId, LocalDateTime cursor,
                                                                   Boolean isChecked, Integer size,
                                                                   UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();

        List<NotificationResponse> notifications = notificationRepository.finaAllByReceiverIdInProject(memberId,
                projectId, cursor,
                isChecked, size + 1);

        Boolean hasMore = notifications.size() > size;
        LocalDateTime nextCursor = null;
        if (hasMore) {
            notifications.remove(notifications.size() - 1);
            nextCursor = notifications.get(notifications.size() - 1).getCreatedAt();
        }
        return new CommonCursorPagingResponse<>(size, nextCursor, hasMore, notifications);
    }

    @Transactional
    public NotificationResponse check(Long notificationId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.NOTIFICATION_NOT_FOUND));
        validateOwnershipOfNotification(notification, memberId);
        notification.check();
        notification = notificationRepository.save(notification);

        Project project = projectRepository.findById(notification.getProjectId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.PROJECT_NOT_FOUND));
        MemberProject sender = memberProjectRepository.findByMemberIdAndProjectId(notification.getSenderId(),
                        notification.getProjectId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        MemberProject receiver = memberProjectRepository.findByMemberIdAndProjectId(notification.getReceiverId(),
                        notification.getProjectId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_PROJECT_NOT_FOUND));
        return NotificationResponse.from(notification, project, sender, receiver);
    }

    @Transactional
    public void delete(Long notificationId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.NOTIFICATION_NOT_FOUND));
        validateOwnershipOfNotification(notification, memberId);
        notification.delete();
    }

    private static void validateOwnershipOfNotification(Notification notification, Long memberId) {
        if (!notification.getReceiverId().equals(memberId)) {
            throw new NotEnoughPrivilegeException(ExceptionMessage.MEMBER_UNAUTHENTICATED);
        }
    }
}