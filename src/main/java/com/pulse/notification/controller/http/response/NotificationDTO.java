package com.pulse.notification.controller.http.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {

    private Long id;

    private Long memberId;  // 알림을 받을 사용자 ID

    private Long typeId; // 알림 타입 ID (예: 좋아요, 댓글 등)

    private String message; // 알림 메시지

    private boolean read; // 읽음 상태

    private LocalDateTime createdAt; // 알림 생성 시간

}
