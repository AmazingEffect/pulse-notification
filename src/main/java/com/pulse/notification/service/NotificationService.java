package com.pulse.notification.service;

import com.pulse.notification.controller.http.response.NotificationDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {

    Mono<NotificationDTO> createNotification(Long memberId, Long typeId, String message);

    Flux<NotificationDTO> getUnreadNotifications(Long memberId);

    Flux<NotificationDTO> getAllNotifications(Long memberId);

    Mono<Void> markAsRead(Long notificationId);

}
