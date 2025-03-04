package com.groo.chatapp;

import com.groo.chatapp.domain.chat.dto.ChatRoomReqDto;
import com.groo.chatapp.domain.chat.service.ChatRoomService;
import com.groo.chatapp.domain.member.dto.MemberDto;
import com.groo.chatapp.domain.member.dto.MemberRegReqDto;
import com.groo.chatapp.domain.member.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final ChatRoomService chatRoomService;

    @Override
    public void run(String... args) throws Exception {
        // 기본 테스트 계정 추가
        //if (!authService.existsByUsername("test")) {
        MemberRegReqDto dto = new MemberRegReqDto("test", "groo","1");
        authService.signup(dto);
        //}

        // 기본 채팅방 생성
        //if (chatRoomService.getChatRooms().isEmpty()) {
        MemberDto memberReq = new MemberDto(1L,"test","groo");

        ChatRoomReqDto reqDto = new ChatRoomReqDto("hello init");
        chatRoomService.createChatRoom(reqDto, memberReq);
        //}
    }
}
