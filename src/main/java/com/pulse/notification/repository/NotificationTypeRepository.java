package com.pulse.notification.repository;

import com.pulse.notification.entity.NotificationType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface NotificationTypeRepository extends ReactiveCrudRepository<NotificationType, Long> {

}
