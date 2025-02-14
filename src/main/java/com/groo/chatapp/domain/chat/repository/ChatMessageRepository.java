package com.groo.chatapp.domain.chat.repository;

import com.groo.chatapp.domain.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, BigInteger> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedDateAsc(Long id);
}
