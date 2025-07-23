package com.groo.chatapp.domain.chat.service;

import com.groo.chatapp.common.code.ErrorCode;
import com.groo.chatapp.common.exception.EntityNotFoundException;
import com.groo.chatapp.common.redis.RedisKeyGenerator;
import com.groo.chatapp.common.redis.RedisService;
import com.groo.chatapp.common.valid.NicknameValidator;
import com.groo.chatapp.domain.chat.ChatRoom;
import com.groo.chatapp.domain.chat.dto.ChatMessageDto;
import com.groo.chatapp.domain.chat.dto.ChatRoomReqDto;
import com.groo.chatapp.domain.chat.dto.ChatRoomResDto;
import com.groo.chatapp.domain.chat.repository.ChatRoomRepository;
import com.groo.chatapp.domain.member.Member;
import com.groo.chatapp.domain.member.dto.MemberDto;
import com.groo.chatapp.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MemberService memberService;
    private final ChatMessageService chatMessageService;
    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NicknameValidator nicknameValidator;

    @Transactional
    public ChatRoomResDto createChatRoom(ChatRoomReqDto reqDto, MemberDto memberDto) {
        // todo 레디스 member 객체 캐싱되도록 수정되었으니 레디스에서 캐싱해오도록 수정
        Member member = memberService.findByEmail(memberDto.getEmail());
        ChatRoom chatRoom = chatRoomRepository.save(reqDto.toEntity(reqDto, member));
        return ChatRoomResDto.fromEntity(chatRoom);
    }

    public List<ChatRoomResDto> findChatRooms() {
        return chatRoomRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .map(ChatRoomResDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ChatRoomResDto enterChatRoom(Long roomId, String firstId, int size) {
        ChatRoom chatRoom = getChatRoom(roomId);
        List<ChatMessageDto> recentMessages = chatMessageService.getRecentMessages(roomId, firstId, size);

        return ChatRoomResDto.fromEntity(chatRoom, recentMessages);
    }

    private ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CHATROOM_NOT_FOUND));
    }


    public void addUserToChatRoom(Long roomId, String nickname) {
        /*if (!nicknameValidator.isValidNickname(nickname)) {
            throw new InvalidValueException(ErrorCode.INVALID_NICKNAME);
        }*/

        Set<String> users = addUserToRoomAndReturnUsers(roomId, nickname);
        sendUpdatedUserList(roomId, users);
    }

    private Set<String> addUserToRoomAndReturnUsers(Long roomId, String nickname) {
        String key = RedisKeyGenerator.getChatRoomKey(roomId);
        Set<String> joinUsers = redisService.getChatRoomUsers(key);

        if (joinUsers.contains(nickname)) {
            return joinUsers;
        }

        notifyUserJoin(roomId, nickname);
        redisService.addUserToChatRoom(key, nickname);

        return redisService.getChatRoomUsers(key);
    }

    private void notifyUserJoin(Long roomId, String nickname) {
        sendUserJoinMessage(roomId, nickname);
    }

    private void sendUserJoinMessage(Long roomId, String nickname) {
        chatMessageService.handleMessage(getMessageDto(roomId, nickname), null);
    }

    private void sendUpdatedUserList(Long roomId, Set<String> users) {
        messagingTemplate.convertAndSend("/topic/"+ roomId + "/users", users);
    }

    public Set<String> leaveChatRoom(Long roomId, String nickname) {
        /*if (!nicknameValidator.isValidNickname(nickname)) {
            throw new InvalidValueException(ErrorCode.INVALID_NICKNAME);
        }*/
        String key = RedisKeyGenerator.getChatRoomKey(roomId);

        redisService.removeUserToChatRoom(key, nickname);
        return redisService.getChatRoomUsers(key);
    }

    private ChatMessageDto getMessageDto(Long roomId, String nickname) {
        return ChatMessageDto.builder()
                .roomId(roomId)
                .type("enter")
                .sender(nickname)
                .build();
    }

}
