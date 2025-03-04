package com.groo.chatapp.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void addUserToChatRoom(String key, String nickname) {
        redisTemplate.opsForSet().add(key, nickname);
    }

    public void removeUserToChatRoom(String key, String nickname) {
        redisTemplate.opsForSet().remove(key, nickname);
    }

    public Set<String> getChatRoomUsers(String key) {
        return redisTemplate.opsForSet().members(key);
    }
}
