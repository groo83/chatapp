package com.groo.chatapp.domain.chat.service;

import com.groo.chatapp.common.code.ErrorCode;
import com.groo.chatapp.domain.chat.ChatRoom;
import com.groo.chatapp.domain.chat.dto.ChatMessageDto;
import com.groo.chatapp.domain.chat.dto.ChatRoomReqDto;
import com.groo.chatapp.domain.chat.dto.ChatRoomResDto;
import com.groo.chatapp.domain.chat.repository.ChatMessageRepository;
import com.groo.chatapp.domain.chat.repository.ChatRoomRepository;
import com.groo.chatapp.domain.member.Member;
import com.groo.chatapp.domain.member.dto.MemberDto;
import com.groo.chatapp.domain.member.repository.MemberRepository;
import com.groo.chatapp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final ChatMessageRepository messageRepository;
    private final MemberRepository memberRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private static final String CHATROOM_KEY_PREFIX = "chatroom:";
    private static final String CHATROOM_KEY_USERS = ":users";

    public void createChatRoom(ChatRoomReqDto reqDto) {
        createChatRoom(reqDto, null);
    }

    @Transactional
    public ChatRoomResDto createChatRoom(ChatRoomReqDto reqDto, MemberDto memberDto) {
        Member member = getMember(memberDto);
        ChatRoom chatRoom = chatRoomRepository.save(reqDto.toEntity(reqDto, member));
        return ChatRoomResDto.fromEntity(chatRoom);
    }

    public List<ChatRoomResDto> findChatRooms() {
        return chatRoomRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .map(ChatRoomResDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ChatRoomResDto enterChatRoom(Long roomId) {
        ChatRoom chatRoom = getChatRoom(roomId);
        List<ChatMessageDto> result = messageRepository.findByChatRoomIdOrderByCreatedDateAsc(roomId)
                .stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());

        return ChatRoomResDto.fromEntity(chatRoom, result);
    }

    private ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CHATROOM));
    }

    private String getChatRoomKey(Long roomId) {
        return CHATROOM_KEY_PREFIX + roomId + CHATROOM_KEY_USERS;
    }

    public Set<String> joinChatRoom(Long roomId, String nickname) {
        /*if (!isValidNickname(nickname)) {
            throw new IllegalArgumentException("Invalid nickname");
        }*/
        String key = getChatRoomKey(roomId);
        addUserToChatRoom(key, nickname);

        return getChatRoomUsers(key);
    }

    public Set<String> leaveChatRoom(Long roomId, String nickname) {
        /*if (!isValidNickname(nickname)) {
            throw new IllegalArgumentException("Invalid nickname");
        }*/
        String key = getChatRoomKey(roomId);

        removeUserToChatRoom(key, nickname);
        return getChatRoomUsers(key);
    }

    private void addUserToChatRoom(String key, String nickname) {
        redisTemplate.opsForSet().add(key, nickname);
    }

    private void removeUserToChatRoom(String key, String nickname) {
        redisTemplate.opsForSet().remove(key, nickname);
    }

    public Set<String> getChatRoomUsers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    private boolean isValidNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return false;
        }
        String regex = "^[a-zA-Z0-9가-힣]{3,20}$";
        return nickname.matches(regex);
    }

    private Member getMember(MemberDto memberDto) {
        return memberDto != null ?
                memberRepository.findByEmail(memberDto.getEmail()).orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND))
                : memberRepository.findByEmail("test").orElseThrow(); // todo remove  테스트를 위함

    }
}
