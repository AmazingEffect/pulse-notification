package com.pulse.notification.controller;

import com.pulse.notification.controller.response.NotificationDTO;
import com.pulse.notification.domain.Notification;
import com.pulse.notification.service.NotificationService;
import com.pulse.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 알림 컨트롤러
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;
    private final SseService sseService;

    /**
     * 사용자의 모든 알림을 조회하는 메서드
     *
     * @param memberId 사용자 ID
     * @return 사용자의 모든 알림
     */
    @GetMapping("/{memberId}")
    public Flux<NotificationDTO> getAllNotifications(@PathVariable Long memberId) {
        return service.getAllNotifications(memberId);
    }


    /**
     * 사용자의 읽지 않은 알림을 조회하는 메서드
     *
     * @param memberId 사용자 ID
     * @return 사용자의 읽지 않은 알림
     */
    @GetMapping("/{memberId}/unread")
    public Flux<NotificationDTO> getUnreadNotifications(@PathVariable Long memberId) {
        return service.getUnreadNotifications(memberId);
    }


    /**
     * 알림 생성 및 SSE로 알림 전송
     *
     * @param notificationId 알림 ID
     * @return 생성된 알림 객체
     */
    @PostMapping("/{notificationId}/read")
    public Mono<Void> markAsRead(@PathVariable Long notificationId) {
        return service.markAsRead(notificationId);
    }


    /**
     * SSE로 클라이언트에게 신호를 보내는 방식
     *
     * @param memberId 사용자 ID
     * @return SSE로 클라이언트에게 신호를 보내는 Flux 객체
     */
    @GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificationDTO> subscribe(@PathVariable Long memberId) {
        return sseService.subscribe(memberId);
    }

}
