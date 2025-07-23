package com.groo.chatapp.domain.chat.service;

import com.groo.chatapp.domain.chat.ChatMessage;
import com.groo.chatapp.domain.chat.dto.ChatMessageDto;
import com.groo.chatapp.domain.chat.repository.H2MessageRepository;
import com.groo.chatapp.domain.chat.repository.RedisMessageRepository;
import com.groo.chatapp.domain.member.CustomUserDetails;
import com.groo.chatapp.domain.member.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisMessageRepository redisMessageRepository;
    private final H2MessageRepository h2MessageRepository;

    public void handleMessage(ChatMessageDto message, Authentication authentication) {
        prepareMessageContent(message);
        notifySubscribers(message);
        persistMessage(message, authentication);
    }

    public void prepareMessageContent(ChatMessageDto message) {
        if (isFileTypeMessage(message)) {
            String fileUrl = generateFileUrl(message.getFileName());
            message.setFileUrl(fileUrl);
        }

        if (isEnterTypeMessage(message)) {
            message.setContent(message.getSender() + "님이 입장하셨습니다.");
        }
    }

    private boolean isFileTypeMessage(ChatMessageDto message) {
        return "file".equals(message.getType());
    }

    private boolean isEnterTypeMessage(ChatMessageDto message) {
        return "enter".equals(message.getType());
    }

    private String generateFileUrl(String fileName) {
        return "/files/download/" + encodeFileName(fileName);
    }

    private String encodeFileName(String fileName) {
        return URLEncoder.encode(fileName, UTF_8).replaceAll("\\+", "%20");
    }

    public void notifySubscribers(ChatMessageDto message) {
        messagingTemplate.convertAndSend("/topic/"+ message.getRoomId() + "/messages", message);
    }

    @Transactional
    public void persistMessage(ChatMessageDto dto, Authentication authentication) {
        String email = (authentication != null) ? getEmail(authentication) : "";
        ChatMessage message = dto.toEntity(email);
        redisMessageRepository.save(message);
    }

    private String getEmail(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        MemberDto memberDto = MemberDto.from(customUserDetails);
        return memberDto.getEmail();
    }

    @Transactional
    public void migrateExpiredMessages(Long roomId) {
        // 1. 특정 기간 이상 지난 메세지 조회
        List<ChatMessage> expiredMessages = redisMessageRepository.getExpiredMessages(roomId);
        if (expiredMessages.isEmpty()) {
            return;
        }

        // todo messageId로 중복저장 여부 판단

        // 2. H2에 저장
        h2MessageRepository.saveExpiredMessages(expiredMessages);

        // 3. Redis에서 삭제
        List<String> messageIds = expiredMessages.stream()
                .map(ChatMessage::getMessageId)
                .collect(Collectors.toList());
        redisMessageRepository.deleteExpiredMessages(roomId, messageIds);
    }

    public List<ChatMessageDto> getRecentMessages(Long roomId, String firstId, int size) {
        List<ChatMessage> resultMessages = new ArrayList<>(redisMessageRepository.getRecentMessages(roomId, firstId, size));

        if (resultMessages.size() < size) {
            int remainingSize = size - resultMessages.size();

            // 최신순 (내림차순)
            List<ChatMessage> h2Messages = h2MessageRepository.getRecentMessages(roomId, firstId, remainingSize);
            // 오름차순으로 정렬
            Collections.reverse(h2Messages);

            resultMessages.addAll(0, h2Messages);
        }

        return resultMessages.stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());
    }
}
