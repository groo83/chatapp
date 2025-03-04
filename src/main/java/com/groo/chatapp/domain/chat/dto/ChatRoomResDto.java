package com.groo.chatapp.domain.chat.dto;

import com.groo.chatapp.domain.chat.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                .nickname(chatRoom.getMember() != null ? chatRoom.getMember().getNickname() : "Unknown")
                .name(chatRoom.getName())
                .messages(messages != null ? messages : new ArrayList<>()) // null 체크
                .createdDate(chatRoom.getCreatedDate())
                .build();
    }
}
