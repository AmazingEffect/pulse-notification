package com.pulse.notification.service.event;

import com.pulse.event_library.event.OutboxEvent;
import com.pulse.event_library.service.OutboxService;
import com.pulse.notification.domain.NotificationOutbox;
import com.pulse.notification.domain.constant.MessageStatus;
import com.pulse.notification.repository.OutboxRepository;
import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository outboxRepository;

    /**
     * OutboxEvent를 저장한다.
     * 상태는 PENDING(대기)으로 저장
     *
     * @param event OutboxEvent
     */
    @Transactional
    @Override
    public void saveOutboxEvent(OutboxEvent event) {
        // 1. 현재 Span에서 Trace ID를 가져옵니다.
        Span currentSpan = Span.current();
        String nowTraceId = currentSpan.getSpanContext().getTraceId();

        // 2. 이벤트 타입에 따라 적절한 토픽 이름을 반환합니다.
        String eventType = getKafkaTopic(event);

        // 3. OutboxEvent를 저장합니다.
        NotificationOutbox outbox = NotificationOutbox.builder()
                .eventType(eventType)
                .payload(event.getId())
                .traceId(nowTraceId)
                .status(MessageStatus.PENDING)
                .build();
        outboxRepository.save(outbox);
    }

    /**
     * OutboxEvent를 처리완료(PROCESSED)로 변경
     *
     * @param event OutboxEvent
     */
    @Transactional
    @Override
    public void markOutboxEventProcessed(OutboxEvent event) {
        String eventType = getKafkaTopic(event);
        NotificationOutbox outbox = outboxRepository.findByPayloadAndEventType(event.getId(), eventType)
                .orElseThrow(() -> new IllegalArgumentException("OutboxEvent not found"));

        if (outbox != null) {
            outbox.changeStatus(MessageStatus.PROCESSED);
            outbox.changeProcessedAt(LocalDateTime.now());
            outboxRepository.save(outbox);
        }
    }

    /**
     * OutboxEvent를 성공(SUCCESS)로 변경
     * 만약 Feign 요청이 성공해서 데이터를 전달한 후 오류가 없다면 이 메서드를 호출한다.
     *
     * @param event OutboxEvent
     */
    @Transactional
    @Override
    public void markOutboxEventSuccess(OutboxEvent event) {
        String eventType = getKafkaTopic(event);
        NotificationOutbox outbox = outboxRepository.findByPayloadAndEventType(event.getId(), eventType)
                .orElseThrow(() -> new IllegalArgumentException("OutboxEvent not found"));

        if (outbox != null) {
            outbox.changeStatus(MessageStatus.SUCCESS);
            outboxRepository.save(outbox);
        }
    }

    /**
     * OutboxEvent를 실패(FAIL)로 변경
     *
     * @param event OutboxEvent
     */
    @Transactional
    @Override
    public void markOutboxEventFailed(OutboxEvent event) {
        String eventType = getKafkaTopic(event);
        NotificationOutbox outbox = outboxRepository.findByPayloadAndEventType(event.getId(), eventType)
                .orElseThrow(() -> new IllegalArgumentException("OutboxEvent not found"));

        if (outbox != null) {
            outbox.changeStatus(MessageStatus.FAIL);
            outboxRepository.save(outbox);
        }
    }

    /**
     * OutboxEvent의 Kafka 토픽을 반환
     * getType() 메서드로 꺼낸 이벤트 타입에 따라 적절한 토픽 이름을 반환한다.
     *
     * @param event OutboxEvent
     * @return Kafka 토픽 이름
     */
    @Transactional
    @Override
    public String getKafkaTopic(OutboxEvent event) {
        // 이벤트 타입이나 기타 조건에 따라 적절한 토픽 이름을 반환
        return switch (event.getEventType()) {
            // member
            case "MemberCreatedOutboxEvent" -> "member-created-outbox";
            case "MemberUpdatedOutboxEvent" -> "member-updated-outbox";
            case "MemberDeletedOutboxEvent" -> "member-deleted-outbox";
            case "MemberNicknameChangeOutboxEvent" -> "member-nickname-change-outbox";
            case "MemberProfileImageChangeOutboxEvent" -> "member-profile-image-change-outbox";
            // content
            case "ContentCreatedOutboxEvent" -> "content-created-outbox";
            case "ContentUpdatedOutboxEvent" -> "content-updated-outbox";
            case "ContentDeletedOutboxEvent" -> "content-deleted-outbox";
            // notification
            case "NotificationCreatedOutboxEvent" -> "notification-created-outbox";
            // default
            default -> "default-topic";
        };
    }

}