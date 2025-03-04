package com.groo.chatapp.domain.chat.scheduler;

import com.groo.chatapp.domain.chat.repository.ChatRoomRepository;
import com.groo.chatapp.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageScheduler {

    private final ChatMessageService messageService;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 매일 0시 0분 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void migrateExpiredMessages() {
        log.info("Start message migration process");

        List<Long> chatRoomIds = chatRoomRepository.findAllChatRoomIds();

        for (Long roomId : chatRoomIds) {
            try {
                messageService.migrateExpiredMessages(roomId);
                log.info("Migrated expired messages for chatRoomId={}", roomId);

            } catch (Exception e) {
                log.error("Faild to migrate messages for chatRoomId={}", roomId);
            }
        }

        log.info("Message migration process completed.");
    }
}
