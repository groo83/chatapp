package com.groo.chatapp.domain.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "chat_message", indexes = {
        @Index(name = "idx_chat_room_id_created_date", columnList = "chat_room_id, created_date"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage { // extends BaseEntity - 레디스 timestamp 사용으로 주석처리

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(nullable = false, unique = true)
    private String messageId; // Redis에서 사용하는 ID : 중복 저장 방지

    private Long chatRoomId;

    private String content;

    //@ManyToOne
    //private Member member;

    private String email;

    private String nickname;

    private String type;

    private String fileUrl;

    private String fileName;

    @Column(updatable = false)
    protected LocalDateTime createdDate;

    @Builder
    public ChatMessage(Long chatRoomId, String messageId, String content, String fileUrl, String fileName, String nickname, String email, String type, LocalDateTime createdDate) {
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.nickname = nickname;
        this.email = email;
        this.messageId = messageId;
        this.type = type;
        this.createdDate = createdDate;
    }
}
