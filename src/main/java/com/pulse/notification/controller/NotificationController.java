package com.pulse.notification.controller;

import com.pulse.notification.domain.Notification;
import com.pulse.notification.service.NotificationService;
import com.pulse.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;
    private final SseService sseService;

    @GetMapping("/{memberId}")
    public Flux<Notification> getAllNotifications(@PathVariable Long memberId) {
        return service.getAllNotifications(memberId);
    }


    @GetMapping("/{memberId}/unread")
    public Flux<Notification> getUnreadNotifications(@PathVariable Long memberId) {
        return service.getUnreadNotifications(memberId);
    }


    @PostMapping("/{notificationId}/read")
    public Mono<Void> markAsRead(@PathVariable Long notificationId) {
        return service.markAsRead(notificationId);
    }


    /**
     * SSE로 클라이언트에게 신호를 보내는 방식
     *
     * @param memberId
     * @return
     */
    @GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Notification> subscribe(@PathVariable Long memberId) {
        return sseService.subscribe(memberId);
    }

}
