package com.groo.chatapp.domain.chat.dto;

import com.groo.chatapp.domain.chat.ChatMessage;
import com.groo.chatapp.domain.chat.ChatRoom;
import com.groo.chatapp.domain.member.Member;
import com.groo.chatapp.domain.member.dto.MemberRegResDto;
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
    private String fileData;  // base64 인코딩 데이터
    private Long roomId; // 채팅방 id

    public static ChatMessageDto fromEntity(ChatMessage message) {
        return ChatMessageDto.builder()
                .sender(message.getMember().getNickname())
                .type(message.getType())
                .content(message.getContent())
                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())
                .build();
    }

    public ChatMessage toEntity(@Valid ChatMessageDto reqDto, Member member) {
        return ChatMessage.builder()
                .member(member)
                .chatRoomId(reqDto.getRoomId())
                .content(reqDto.getContent())
                .fileUrl(reqDto.getFileUrl())
                .fileName(reqDto.getFileName())
                .type(reqDto.getType())
                .build();
    }
}
