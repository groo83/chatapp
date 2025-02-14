package com.groo.chatapp.domain.chat.web;

import com.groo.chatapp.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRoomSocketController {

    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/{roomId}/join")
    public void handleUserJoin(@DestinationVariable("roomId") Long roomId, String nickname) {
        Set<String> users = chatRoomService.joinChatRoom(roomId, nickname);
        messagingTemplate.convertAndSend("/topic/"+ roomId + "/users", users);
    }

    @MessageMapping("/{roomId}/leave")
    @SendTo("/topic/{roomId}/users")
    public ResponseEntity<Set<String>> handleUserLeave(@PathVariable("roomId") Long roomId, String nickname) {
        return ResponseEntity.ok(chatRoomService.leaveChatRoom(roomId, nickname));
    }
}
