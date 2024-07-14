package com.pulse.notification.kafka;

import com.pulse.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "like-events", groupId = "notification-group")
    public void handleLikeEvent(@Payload String message) {
        Long memberId = 1L;
        Long typeId = 1L;
        String content = "You have a new like!";
        notificationService.createNotification(memberId, typeId, content).subscribe();
    }


    @KafkaListener(topics = "comment-events", groupId = "notification-group")
    public void handleCommentEvent(@Payload String message) {
        Long memberId = 1L;
        Long typeId = 1L;
        String content = "You have a new like!";
        notificationService.createNotification(memberId, typeId, content).subscribe();
    }

}
