package com.pulse.notification.service;

import com.pulse.notification.domain.Notification;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE를 통해 알림을 전송하는 서비스 클래스.
 */
@Service
public class SseService {

    // 사용자 ID를 키로 하고, Notification을 전송하기 위한 Sinks.Many 객체를 값으로 갖는 ConcurrentHashMap
    private final Map<Long, Sinks.Many<Notification>> userEmitters = new ConcurrentHashMap<>();

    /**
     * 사용자가 SSE를 통해 알림을 구독할 때 호출되는 메서드.
     * 사용자별로 Sinks.Many 객체를 생성하여 저장하고, Flux<Notification>을 반환한다.
     * 사용자가 구독을 종료하면 맵에서 해당 사용자에 대한 Sinks.Many 객체를 제거한다.
     *
     * @param memberId 사용자 ID
     * @return Flux<Notification> 객체를 반환하여 SSE를 통해 알림을 수신할 수 있게 한다.
     */
    public Flux<Notification> subscribe(Long memberId) {
        // 1. Sinks.Many 객체 생성
        Sinks.Many<Notification> sink = Sinks.many().multicast().onBackpressureBuffer();

        // 2. 사용자 ID를 키로 하고 Sinks.Many 객체를 값으로 하는 맵에 저장
        userEmitters.put(memberId, sink);

        // 3. 사용자가 구독을 종료할 때 맵에서 Sinks.Many 객체를 제거
        return sink.asFlux().doFinally(signalType -> userEmitters.remove(memberId));
    }


    /**
     * 서버에서 알림을 생성했을 때, 해당 알림을 사용자의 SSE 구독자에게 전송하는 메서드.
     * 맵에서 사용자 ID에 해당하는 Sinks.Many 객체를 찾아 알림을 전송한다.
     *
     * @param memberId     알림을 받을 사용자 ID
     * @param notification 전송할 Notification 객체
     */
    public void sendNotification(Long memberId, Notification notification) {
        // 1. 맵에서 사용자 ID에 해당하는 Sinks.Many 객체를 찾음
        Sinks.Many<Notification> sink = userEmitters.get(memberId);

        // 2. Sinks.Many 객체가 존재할 경우 알림 전송
        if (sink != null) {
            sink.tryEmitNext(notification);
        }
    }

}
