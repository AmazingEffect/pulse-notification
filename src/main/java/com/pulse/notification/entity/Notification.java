package com.pulse.notification.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table("notifications")
public class Notification {

    @Id
    private Long id;

    @Column("member_id")
    private Long memberId;  // 알림을 받을 사용자 ID

    @Column("type_id")   // fk
    private Long typeId; // 알림 타입 ID (예: 좋아요, 댓글 등)

    @Column("message")
    private String message; // 알림 메시지

    @Column("read")
    private boolean read; // 읽음 상태

    @Column("created_at")
    private LocalDateTime createdAt; // 알림 생성 시간

    // Factory method
    public static Notification of(Long memberId, Long typeId, String message, boolean read, LocalDateTime createdAt) {
        return new Notification(null, memberId, typeId, message, read, createdAt);
    }

    public void changeReadStatus(boolean read) {
        this.read = read;
    }

}
