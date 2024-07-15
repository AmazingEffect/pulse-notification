package com.pulse.notification.listener.kafka.alarm;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.event_library.event.OutboxEvent;
import com.pulse.notification.config.trace.annotation.TraceOutboxKafka;
import com.pulse.notification.listener.spring.event.CommentCreateEvent;
import com.pulse.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 * 친구 요청 메시지 수신 Kafka 리스너
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FriendRequestMessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationService notificationService;

    /**
     * 유저가 생성되면 이벤트를 수신하고
     * gRPC 클라이언트를 통해 member 서버에 회원 정보를 요청한다.
     *
     * @param record         - Kafka 메시지
     * @param acknowledgment - ack 처리
     * @param partition      - partition
     * @param offset         - offset
     */
    @TraceOutboxKafka
    @KafkaListener(
            topics = {"friend-request-outbox"},
            groupId = "notification-friend-request",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void saveFriendRequestNotification(
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) throws JsonProcessingException {
        // 1. 이벤트 객체로 변환한다.
        String jsonValue = record.value();
        OutboxEvent event = objectMapper.readValue(jsonValue, CommentCreateEvent.class);

        // 2. 필요한 정보를 추출한다.
        Long payload = event.getId(); // 멤버 ID
        Long typeId = 1L; // 알림 타입 ID (예: 좋아요, 댓글 등)
        String content = "You have a new comment!"; // 알림 내용 (enum으로 만들어서 관리할까? 고민중)

        // 3. notificationRepository를 통해 알림 db에 저장한다.
        // todo: 여기서 궁금한게 이렇게 카프카 리스너 내부에서 db를 호출하는게 맞는건지?
        notificationService.createNotification(payload, typeId, content).subscribe();
        // 3. ack 처리
        acknowledgment.acknowledge();
    }

}
