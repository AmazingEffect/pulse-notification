package com.pulse.notification.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Table("member")
public class Member {

    @Id
    private Long id;               // PK

    @Column("email")
    private String email;          // 이메일

    @Column("name")
    private String name;           // 이름

    @Column("nickname")
    private String nickname;       // 닉네임

    @Column("profile_picture_url")
    private String profilePictureUrl; // 프로필 사진

    @Column("status_message")
    private String statusMessage;  // 상태 메시지

}
