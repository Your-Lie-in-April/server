package com.appcenter.timepiece.domain.notification.controller;

import com.appcenter.timepiece.domain.notification.service.NotificationService;
import com.appcenter.timepiece.global.common.dto.CommonResponse;
import com.appcenter.timepiece.global.config.SwaggerApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("")
@SwaggerApiResponses
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "SSE Connect", description = "SSE 커넥션을 맺습니다.")
    @GetMapping(value = "/v1/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(required = false) Long lastEventId,
                                @RequestParam(required = false) Long projectId) {
        log.info("Last-Event-Id: {}", lastEventId);
        // todo: lastEventId -> 유실된 Message 전송
        return notificationService.subscribe(userDetails, lastEventId, projectId);
    }

    @Operation(summary = "이전 알림 조회", description = "DB에 저장된 알림을 조회합니다.")
    @GetMapping(value = "/v1/notifications")
    public CommonResponse<?> getNotifications(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}", required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "false", required = false) Boolean isChecked,
            @RequestParam(defaultValue = "12", required = false) Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("알림 조회에 성공했습니다.",
                notificationService.getNotifications(cursor, isChecked, size, userDetails));
    }

    @Operation(summary = "(프로젝트 내)이전 알림 조회", description = "DB에 저장된 특정 프로젝트의 알림을 조회합니다.")
    @GetMapping(value = "/v1/projects/{projectId}/notifications")
    public CommonResponse<?> getNotificationsInProject(
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}", required = false) LocalDateTime cursor,
            @RequestParam(defaultValue = "false", required = false) Boolean isChecked,
            @RequestParam(defaultValue = "12", required = false) Integer size,
            @PathVariable Long projectId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("알림 조회에 성공했습니다.",
                notificationService.getNotificationsInProject(projectId, cursor, isChecked, size, userDetails));
    }

    @Operation(summary = "알림 삭제", description = "notificationId에 해당하는 알림을 삭제합니다.")
    @DeleteMapping(value = "/v1/notifications/{notificationId}")
    public CommonResponse<Void> delete(@PathVariable Long notificationId,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.delete(notificationId, userDetails);
        return CommonResponse.success("알림 삭제에 성공했습니다.", null);
    }

    @Operation(summary = "알림 읽음 처리", description = "notificationId에 해당하는 알림을 읽음 처리 합니다.")
    @PatchMapping(value = "/v1/projects/notifications/{notificationId}")
    public CommonResponse<?> check(@PathVariable Long notificationId,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("알림 읽을 처리에 성공했습니다.",
                notificationService.check(notificationId, userDetails));
    }
}
