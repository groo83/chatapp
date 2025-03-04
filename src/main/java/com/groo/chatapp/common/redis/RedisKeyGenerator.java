package com.groo.chatapp.common.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // 인스턴스화 방지
public class RedisKeyGenerator {

    private static final String CHAT_KEY_PREFIX = "chat:";
    private static final String CHATROOM_KEY_PREFIX = "room:";
    private static final String CHAT_KEY_MESSAGES = ":messages";
    private static final String CHATROOM_KEY_USERS = ":users";
    private static final String MEMBER_KEY_PREFIX = "member:";

    public static String getChatMessageStreamKey(Long roomId) {
        return CHAT_KEY_PREFIX + CHATROOM_KEY_PREFIX + roomId + CHAT_KEY_MESSAGES;
    }

    public static String getChatRoomKey(Long roomId) {
        return CHAT_KEY_PREFIX + CHATROOM_KEY_PREFIX + roomId + CHATROOM_KEY_USERS;
    }

    public static String getMemberKey(String email) {
        return MEMBER_KEY_PREFIX + email;
    }

}