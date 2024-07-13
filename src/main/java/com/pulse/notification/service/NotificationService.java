package com.pulse.notification.service;

import com.pulse.notification.domain.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {

    Mono<Notification> createNotification(Long memberId, Long typeId, String message);

    Flux<Notification> getUnreadNotifications(Long memberId);

    Flux<Notification> getAllNotifications(Long memberId);

    Mono<Void> markAsRead(Long notificationId);

}
