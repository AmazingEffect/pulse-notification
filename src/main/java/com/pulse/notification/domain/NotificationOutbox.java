package com.pulse.notification.domain;

import com.pulse.notification.domain.constant.MessageStatus;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 이벤트 발행 및 Kafka 메시지 송/수신을 관리하기 위한 Outbox 엔티티
 */
@Builder
@Getter
@Table(name = "notification_outbox")
public class NotificationOutbox {

    @Id
    private Long id;

    @Column("event_type")
    private String eventType;   // 토픽정보 ex.MemberCreatedEvent

    @Column("payload")
    private Long payload;       // 이벤트 내부의 id 필드를 저장. ex) memberId: 1L

    @Column("trace_id")
    private String traceId;     // Kafka 메시지 처리 시, traceId

    @Column("message_status")
    private MessageStatus status;      // Kafka 메시지 처리 상태 (예: PENDING, PROCESSED, SUCCESS, FAIL)

    @Column("processed_at")
    private LocalDateTime processedAt; // Kafka 메시지 처리 시간 (처리된 경우)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationOutbox that)) return false;
        return id != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public void changeStatus(MessageStatus messageStatus) {
        this.status = messageStatus;
    }

    public void changeProcessedAt(LocalDateTime now) {
        this.processedAt = now;
    }

}