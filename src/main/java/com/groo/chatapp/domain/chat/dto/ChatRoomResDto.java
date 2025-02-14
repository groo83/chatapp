package com.groo.chatapp.domain.chat.dto;

import com.groo.chatapp.domain.chat.ChatMessage;
import com.groo.chatapp.domain.chat.ChatRoom;
import com.groo.chatapp.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatRoomResDto {

    private Long chatRoomId;

    private String nickname;

    private String name;

    private LocalDateTime createdDate;

    private List<ChatMessageDto> messages;

    public static ChatRoomResDto fromEntity(ChatRoom chatRoom) {
        return fromEntity(chatRoom, null);
    }

    public static ChatRoomResDto fromEntity(ChatRoom chatRoom, List<ChatMessageDto> messages) {
        return ChatRoomResDto.builder()
                .chatRoomId(chatRoom.getId())
                .nickname(chatRoom.getMember().getNickname())
                .name(chatRoom.getName())
                .messages(messages)
                .createdDate(chatRoom.getCreatedDate())
                .build();
    }
}
