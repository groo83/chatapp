package com.groo.chatapp.domain.chat.mapper;

import com.groo.chatapp.domain.chat.ChatMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageMapper {

    public static Map<String, String> toMap(ChatMessage message) {
        Map<String, String> messageData = new HashMap<>();

        messageData.put("email", String.valueOf(message.getEmail()));
        messageData.put("nickname", message.getNickname());
        messageData.put("content", message.getContent());
        messageData.put("type", message.getType());
        messageData.put("fileUrl", message.getFileUrl());
        messageData.put("fileName", message.getFileName());
        messageData.put("timestamp", LocalDateTime.now().toString());

        return messageData;
    }
}
