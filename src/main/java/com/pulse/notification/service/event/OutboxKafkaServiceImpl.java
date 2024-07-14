package com.pulse.notification.service.event;

import com.pulse.event_library.service.OutboxKafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OutboxKafkaServiceImpl implements OutboxKafkaService {

    @Override
    public void updateOutboxStatus() {

    }

}
