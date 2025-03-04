package com.groo.chatapp.domain.chat.repository;

import com.groo.chatapp.domain.chat.ChatMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Repository
public class H2MessageRepository implements ChatMessageRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ChatMessage> getRecentMessages(Long roomId, String firstId, int size) {
        // todo 쿼리 조건 수정
        return em.createQuery(
                "SELECT c FROM ChatMessage c WHERE c.chatRoomId = :roomId " +
                        //"AND c.messageId < :firstId " + // 페이징 추가
                        "ORDER BY c.createdDate DESC", ChatMessage.class)
                .setParameter("roomId", roomId)
               //.setParameter("firstId", firstId)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public BigInteger save(ChatMessage message) {
        em.persist(message);
        em.flush();
        return message.getId();
    }

    @Transactional
    public void saveExpiredMessages(List<ChatMessage> messages) {
        for (int i = 0; i < messages.size(); i++) {
            em.persist(messages.get(i));

            // 일정 개수마다 flush & clear 실행 (Batch Insert 최적화)
            if (i % 50 == 0) {
                em.flush();
                em.clear();
            }
        }
    }

}
