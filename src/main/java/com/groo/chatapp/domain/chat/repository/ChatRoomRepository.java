package com.groo.chatapp.domain.chat.repository;

import com.groo.chatapp.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByOrderByCreatedDateDesc();

    @Query("SELECT c.id FROM ChatRoom c")
    List<Long> findAllChatRoomIds();

}
