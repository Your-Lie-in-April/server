package com.appcenter.timepiece.service;

import com.appcenter.timepiece.common.exception.ExceptionMessage;
import com.appcenter.timepiece.common.exception.NotEnoughPrivilegeException;
import com.appcenter.timepiece.common.exception.NotFoundElementException;
import com.appcenter.timepiece.common.security.CustomUserDetails;
import com.appcenter.timepiece.domain.Member;
import com.appcenter.timepiece.domain.MemberProject;
import com.appcenter.timepiece.domain.Notification;
import com.appcenter.timepiece.domain.Project;
import com.appcenter.timepiece.dto.notify.NotificationResponse;
import com.appcenter.timepiece.repository.MemberProjectRepository;
import com.appcenter.timepiece.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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


    public SseEmitter subscribe(UserDetails userDetails) {
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

        return emitter;
    }

    public void notifyScheduleChanging(Project project, Member sender) {
        String message = project.getTitle() + "의 " + sender.getNickname() + "이 스케줄을 변경했습니다.";
        List<MemberProject> memberProjects = memberProjectRepository.findAllByProjectId(project.getId());

        NotifyProjectCollection notifyProjectCollection = new NotifyProjectCollection(memberProjects);
        notifyProjectCollection.excludeSender(sender).forEach(memberProject -> {
            sendNotification(message, project, memberProject.getMember(), sender, Notification.NotificationType.SCHEDULE);
        });
    }

    public void notifySigning(Project project, Member sender) {
        String message = project.getTitle() + "에 " + sender.getNickname() + "가 가입했습니다.";
        List<MemberProject> memberProjects = memberProjectRepository.findAllByProjectId(project.getId());

        NotifyProjectCollection notifyProjectCollection = new NotifyProjectCollection(memberProjects);
        notifyProjectCollection.excludeSender(sender).forEach(memberProject -> {
            sendNotification(message, project, memberProject.getMember(), sender, Notification.NotificationType.SIGN);
        });
    }

    public void notifyBecomingOwner(Project project, Member receiver, Member sender) {
        String message = project.getTitle() + "의 관리자가 되었습니다.";
        sendNotification(message, project, receiver, sender, Notification.NotificationType.SYSTEM);
    }

    private void sendNotification(String message, Project project, Member receiver, Member sender, Notification.NotificationType type) {
        Notification notification = Notification.of(message, project, receiver, sender, type);
        notification = notificationRepository.save(notification);
        publishNotification(notification);
    }

    protected void publishNotification(Notification notification) {
        NotificationResponse notificationResponse = NotificationResponse.from(notification);
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
    public List<NotificationResponse> getNotifications(Integer page, Integer size, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        Sort strategy = Sort.by(Sort.Direction.ASC, "isChecked")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));

        return notificationRepository.findByReceiverIdAndIsDeletedIsFalse(PageRequest.of(page, size, strategy), memberId).stream()
                .map(NotificationResponse::from).toList();
    }

    @Transactional
    public List<NotificationResponse> getNotificationsInProject(Long projectId, Integer page, Integer size, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        Sort strategy = Sort.by(Sort.Direction.ASC, "isChecked")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByReceiverIdAndProjectIdAndIsDeletedIsFalse(PageRequest.of(page, size, strategy), memberId, projectId).stream()
                .map(NotificationResponse::from).toList();
    }

    @Transactional
    public NotificationResponse check(Long notificationId, UserDetails userDetails) {
        Long memberId = ((CustomUserDetails) userDetails).getId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.NOTIFICATION_NOT_FOUND));
        validateOwnershipOfNotification(notification, memberId);
        notification.check();
        notification = notificationRepository.save(notification);
        return NotificationResponse.from(notification);
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
        if (!notification.getReceiver().getId().equals(memberId))
            throw new NotEnoughPrivilegeException(ExceptionMessage.MEMBER_UNAUTHENTICATED);
    }
}