package com.pulse.notification.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("notification_types")
public class NotificationType {

    @Id
    private Long id; // 알림 타입 ID

    @Column("name")
    private String name; // 알림 타입 이름 (예: 좋아요, 댓글 등)

}
