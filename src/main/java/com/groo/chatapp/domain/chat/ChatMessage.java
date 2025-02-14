package com.groo.chatapp.domain.chat;

import com.groo.chatapp.common.entity.BaseEntity;
import com.groo.chatapp.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Entity
@Getter
@Table(name = "chat_message", indexes = {
        @Index(name = "idx_chat_room_id_created_date", columnList = "chat_room_id, created_date"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    private Long chatRoomId;

    private String content;

    @ManyToOne
    private Member member;

    private String type;

    private String fileUrl;

    private String fileName;

    @Builder
    public ChatMessage(Long chatRoomId, String content, String fileUrl, String fileName, Member member, String type) {
        this.chatRoomId = chatRoomId;
        this.content = content;
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.member = member;
        this.type = type;
    }
}
