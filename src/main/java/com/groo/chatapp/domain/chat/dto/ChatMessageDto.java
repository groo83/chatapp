package com.groo.chatapp.domain.chat.dto;

import com.groo.chatapp.domain.chat.ChatMessage;
import jakarta.validation.Valid;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {

    private String sender;
    private String type;   // 메시지 타입 ("message" 또는 "file")
    private String content;  // 메시지 내용
    private String fileName;  // 파일 이름
    private String fileUrl;  // Download URL
    private Long roomId; // 채팅방 id
    private String messageId; // 레디스 메세지 id

    public static ChatMessageDto fromEntity(ChatMessage message) {
        return ChatMessageDto.builder()
                .sender(message.getNickname())
                .type(message.getType())
                .content(message.getContent())
                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())
                .messageId(message.getMessageId())
                .build();
    }

    public ChatMessage toEntity(@Valid ChatMessageDto reqDto, String email) {
        return ChatMessage.builder()
                .chatRoomId(reqDto.getRoomId())
                .nickname(reqDto.getSender())
                .email(email)
                .content(reqDto.getContent())
                .fileUrl(reqDto.getFileUrl())
                .fileName(reqDto.getFileName())
                .type(reqDto.getType())
                .messageId(reqDto.getMessageId())
                .build();
    }
}
