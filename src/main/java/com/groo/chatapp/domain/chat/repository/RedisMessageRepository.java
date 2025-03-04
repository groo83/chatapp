package com.groo.chatapp.domain.chat.repository;

import com.groo.chatapp.common.redis.RedisKeyGenerator;
import com.groo.chatapp.domain.chat.ChatMessage;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static com.groo.chatapp.domain.chat.mapper.ChatMessageMapper.toMap;

@Repository
public class RedisMessageRepository implements ChatMessageRepository {

    private static final long MESSAGE_EXPIRATION_DAYS = 1; // 보관기간(일)
    private static final int MESSAGE_PAGE_DEFAULT_SIZE = 20;

    private final StreamOperations<String, String, String> streamOps; // Object, Object형

    public RedisMessageRepository(StringRedisTemplate redisTemplate) {
        this.streamOps = redisTemplate.opsForStream();
    }

    @Override
    public String save(ChatMessage message) {
        String streamKey = RedisKeyGenerator.getChatMessageStreamKey(message.getChatRoomId());

        Map<String, String> messageData = toMap(message);
        RecordId messageId = streamOps.add(streamKey, messageData);

        return messageId.getValue();
    }

    @Override
    public List<ChatMessage> getRecentMessages(Long roomId, String firstId, int size) {
        String streamKey = RedisKeyGenerator.getChatMessageStreamKey(roomId);

        Range<String> range = (firstId == null) ?
                Range.unbounded() :
                Range.open("-", firstId); // firstId 미포함

        List<MapRecord<String, String, String>> records = streamOps.reverseRange(streamKey, range, Limit.limit().count((size == 0) ? MESSAGE_PAGE_DEFAULT_SIZE : size));

        return records.stream()
                .sorted((a,b) -> a.getId().getValue().compareTo(b.getId().getValue())) // desc 정렬 (과거-최신)
                .map(recode -> toChatMessage(roomId, recode))
                .toList();
    }

    public List<ChatMessage> getExpiredMessages(Long roomId) {
        //long fewDaysAgo = System.currentTimeMillis() - MESSAGE_EXPIRATION_DAYS * 24 * 60 * 60 * 1000;
        String streamKey = RedisKeyGenerator.getChatMessageStreamKey(roomId);

        // List<MapRecord<String, String, String>> messages = streamOps.read(StreamReadOptions.empty().count(5), StreamOffset.fromStart(streamKey));
        // redis 에서 특정 조건 직접 조회
        long fewDaysAgoMillis = Instant.now().minusSeconds(MESSAGE_EXPIRATION_DAYS * 24 * 3600).toEpochMilli();
        String fewDaysAgoId = fewDaysAgoMillis + "-0";

        Range<String> range = Range.leftOpen("0", fewDaysAgoId);
        List<MapRecord<String, String, String>> records = streamOps.range(streamKey, range, Limit.limit().count(5));

        return records.stream()
                .map(recode -> toChatMessage(roomId, recode))
                .collect(Collectors.toList());
    }

    public void deleteExpiredMessages(Long roomId, List<String> messageIds) {
        String streamKey = RedisKeyGenerator.getChatMessageStreamKey(roomId);
        streamOps.delete(streamKey, messageIds.toArray(new String[0]));
    }

    private ChatMessage toChatMessage(Long roomId, MapRecord<String, String, String> record) {
        return ChatMessage.builder()
                .chatRoomId(roomId)
                .content(record.getValue().get("content"))
                .fileUrl(record.getValue().get("fileUrl"))
                .fileName(record.getValue().get("fileName"))
                .email(record.getValue().get("email"))
                .nickname(record.getValue().get("nickname"))
                .type(record.getValue().get("type"))
                .messageId(record.getId().getValue())
                .createdDate(Instant.ofEpochMilli(Long.parseLong(record.getId().getValue().split("-")[0]))  // ID에서 timestamp 추출
                        .atZone(ZoneId.of("UTC")).toLocalDateTime())
                .build();
    }
}
