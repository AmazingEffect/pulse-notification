package com.pulse.notification.config.trace.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Outbox 테이블에 메시지 처리 기록을 남기는 KafkaConsumer의 메서드에 적용해서 Span을 적용하는 어노테이션
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TraceOutboxKafka {
}