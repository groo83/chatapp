package com.groo.chatapp.domain.chat.dto;

import com.groo.chatapp.domain.chat.ChatRoom;
import com.groo.chatapp.domain.member.Member;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor // TestDataInitializer 를 위해 추가
public class ChatRoomReqDto {

    private String roomName;

    public ChatRoom toEntity(@Valid ChatRoomReqDto reqDto, Member member) {
        return ChatRoom.builder()
                .member(member)
                .name(reqDto.getRoomName())
                .build();
    }
}
