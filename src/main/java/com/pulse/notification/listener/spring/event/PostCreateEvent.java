package com.pulse.notification.listener.spring.event;

import com.pulse.event_library.event.OutboxEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateEvent implements OutboxEvent {

    private Long id;

    @Override
    public String getEventType() {
        return "PostCreatedOutboxEvent";
    }

}