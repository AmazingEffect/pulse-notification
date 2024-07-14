package com.pulse.notification.listener.kafka.outbox.member;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.event_library.event.OutboxEvent;
import com.pulse.notification.config.trace.annotation.TraceZeroPayloadKafka;
import com.pulse.notification.controller.grpc.GrpcMemberClient;
import com.pulse.notification.grpc.MemberProto;
import com.pulse.notification.listener.spring.event.MemberCreateEvent;
import io.opentelemetry.context.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 * 회원이 생성되면 이벤트를 수신하는 Kafka 리스너
 * zeroPayload - gRPC 클라이언트를 통해 member 서버에 회원 정보를 요청한다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MemberCreateMessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GrpcMemberClient grpcMemberClient;

    /**
     * 유저가 생성되면 이벤트를 수신하고
     * gRPC 클라이언트를 통해 member 서버에 회원 정보를 요청한다.
     *
     * @param record         - Kafka 메시지
     * @param acknowledgment - ack 처리
     * @param partition      - partition
     * @param offset         - offset
     */
    @TraceZeroPayloadKafka
    @KafkaListener(
            topics = {"member-created-outbox"},
            groupId = "content-notification-member-create",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenMemberCreate(
            ConsumerRecord<String, String> record,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) throws JsonProcessingException {
        // 1. 이벤트 객체로 변환한다.
        String jsonValue = record.value();
        OutboxEvent event = objectMapper.readValue(jsonValue, MemberCreateEvent.class);

        // 2. gRPC 요청을 보내고 응답 받기
        MemberProto.MemberRetrieveResponse result =
                grpcMemberClient.getMemberById(event.getId(), Context.current());
        log.info(String.valueOf(result));

        // 3. ack 처리
        acknowledgment.acknowledge();
    }

}
