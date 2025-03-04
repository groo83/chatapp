package com.groo.chatapp.domain.chat.api;

import com.groo.chatapp.common.annotation.UserInfo;
import com.groo.chatapp.common.dto.DataResponse;
import com.groo.chatapp.domain.chat.dto.ChatRoomReqDto;
import com.groo.chatapp.domain.chat.dto.ChatRoomResDto;
import com.groo.chatapp.domain.chat.service.ChatRoomService;
import com.groo.chatapp.domain.member.dto.MemberDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public DataResponse<ChatRoomResDto> create(@Valid @RequestBody ChatRoomReqDto reqDto, @UserInfo MemberDto memberDto) {
        return DataResponse.create(chatRoomService.createChatRoom(reqDto, memberDto));
    }

    @GetMapping
    @ResponseStatus(value = HttpStatus.OK)
    public DataResponse<List<ChatRoomResDto>> findChatRooms() {
        return DataResponse.create(chatRoomService.findChatRooms());
    }

    @GetMapping("/{roomId}/messages")
    @ResponseStatus(value = HttpStatus.OK)
    public DataResponse<ChatRoomResDto> enterChatRoom(@PathVariable("roomId") Long roomId,
                                                      @RequestParam(name = "firstId", required = false) String firstId,
                                                      @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        return DataResponse.create(chatRoomService.enterChatRoom(roomId, firstId, size));
    }
}
