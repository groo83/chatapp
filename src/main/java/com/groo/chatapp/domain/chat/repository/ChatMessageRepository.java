package com.groo.chatapp.domain.chat.repository;

import com.groo.chatapp.domain.chat.ChatMessage;
import java.util.List;

public interface ChatMessageRepository {
    List<ChatMessage> getRecentMessages(Long roomId, String firstId, int size);
    <T> T save(ChatMessage message);
}
