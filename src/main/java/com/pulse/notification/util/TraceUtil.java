package com.pulse.notification.util;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * MSA의 분산 추적을 위해 Trace Context를 추출하는 유틸리티 클래스
 * Jaeger를 사용하여 분산 추적을 수행하고 있으며, Kafka 메시지에서 traceparent 헤더를 추출하여 SpanContext를 생성한다.
 */
@Slf4j
@Component
public class TraceUtil {

    /**
     * Kafka 메시지에서 traceparent 헤더를 추출하여 기존의 Context와 연결한다.
     *
     * @param record Kafka 메시지
     * @return 추출한 SpanContext를 포함한 Context
     */
    public Context extractContextFromRecord(ConsumerRecord<String, String> record) {
        String traceParent = null;

        // kafka 헤더에서 traceparent를 추출한다.
        for (Header header : record.headers()) {
            if ("traceparent".equals(header.key())) {
                traceParent = new String(header.value(), StandardCharsets.UTF_8);
                log.info("Extracted traceparent: {}", traceParent);
                break;
            }
        }

        if (traceParent != null) {
            String[] parts = traceParent.split("-");
            if (parts.length < 4) {
                log.error("Invalid traceparent header: {}", traceParent);
                return Context.current();
            }

            String traceId = parts[1];
            String spanId = parts[2];
            String traceFlags = parts[3];

            // 추출한 traceId, spanId를 사용하여 SpanContext를 생성한다.
            SpanContext spanContext = SpanContext.createFromRemoteParent(
                    traceId,
                    spanId,
                    TraceFlags.fromHex(traceFlags, 0),
                    TraceState.getDefault()
            );

            log.info("Created SpanContext with traceId: {}, spanId: {}", traceId, spanId);
            return Context.current().with(Span.wrap(spanContext));
        } else {
            log.warn("No traceparent header found in the Kafka message");
            return Context.current();
        }
    }

}
