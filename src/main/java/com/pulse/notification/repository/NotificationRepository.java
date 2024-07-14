package com.pulse.notification.repository;

import com.pulse.notification.entity.Notification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long> {

    Flux<Notification> findByMemberIdAndReadFalse(Long memberId);

    Flux<Notification> findByMemberId(Long memberId);

}
