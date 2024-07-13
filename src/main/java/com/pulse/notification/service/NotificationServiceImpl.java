package com.pulse.notification.service;

import com.pulse.notification.domain.Notification;
import com.pulse.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;

    /**
     * 알림 생성 및 SSE로 알림 전송
     *
     * @param memberId 사용자 ID
     * @param typeId   알림 타입 ID
     * @param message  알림 메시지
     * @return 생성된 알림 객체
     */
    @Override
    public Mono<Notification> createNotification(Long memberId, Long typeId, String message) {
        // 알림 생성
        Notification notification = Notification.of(
                memberId,
                typeId,
                message,
                false,
                LocalDateTime.now()
        );
        // 알림 저장 후 SSE로 알림 전송
        return notificationRepository.save(notification)
                .doOnSuccess(savedNotification -> sseService.sendNotification(memberId, savedNotification));
    }

    @Override
    public Flux<Notification> getUnreadNotifications(Long memberId) {
        return notificationRepository.findByMemberIdAndReadFalse(memberId);
    }

    @Override
    public Flux<Notification> getAllNotifications(Long memberId) {
        return notificationRepository.findByMemberId(memberId);
    }

    @Override
    public Mono<Void> markAsRead(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .flatMap(notification -> {
                    notification.changeReadStatus(true);
                    return notificationRepository.save(notification);
                })
                .then();
    }

}
