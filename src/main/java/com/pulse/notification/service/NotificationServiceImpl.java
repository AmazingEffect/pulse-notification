package com.pulse.notification.service;

import com.pulse.notification.controller.response.NotificationDTO;
import com.pulse.notification.domain.Notification;
import com.pulse.notification.mapper.NotificationMapper;
import com.pulse.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * 알림 관련 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationServiceImpl implements NotificationService {

    private final SseService sseService;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    /**
     * 알림 생성 및 SSE로 알림 전송
     *
     * @param memberId 사용자 ID
     * @param typeId   알림 타입 ID
     * @param message  알림 메시지
     * @return 생성된 알림 객체
     */
    @Override
    public Mono<NotificationDTO> createNotification(Long memberId, Long typeId, String message) {
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
                .map(notificationMapper::toDto)
                .doOnSuccess(savedNotification -> sseService.sendNotification(memberId, savedNotification));
    }


    /**
     * 사용자의 읽지 않은 알림 조회
     *
     * @param memberId 사용자 ID
     * @return 읽지 않은 알림 목록
     */
    @Override
    public Flux<NotificationDTO> getUnreadNotifications(Long memberId) {
        return notificationRepository.findByMemberIdAndReadFalse(memberId)
                .map(notificationMapper::toDto);
    }


    /**
     * 사용자의 모든 알림 조회
     *
     * @param memberId 사용자 ID
     * @return 사용자의 모든 알림 목록
     */
    @Override
    public Flux<NotificationDTO> getAllNotifications(Long memberId) {
        return notificationRepository.findByMemberId(memberId)
                .map(notificationMapper::toDto);
    }


    /**
     * 알림을 읽음 처리
     *
     * @param notificationId 알림 ID
     * @return Mono<Void>
     */
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
