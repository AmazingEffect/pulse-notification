package com.pulse.notification.repository;

import com.pulse.notification.domain.NotificationOutbox;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.Optional;

public interface OutboxRepository extends ReactiveCrudRepository<NotificationOutbox, Long> {

    Optional<NotificationOutbox> findByPayloadAndEventType(Long id, String eventType);

}
