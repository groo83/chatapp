package com.groo.chatapp.domain.member.repository;

import com.groo.chatapp.common.redis.RedisKeyGenerator;
import com.groo.chatapp.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * @Cacheable 등 spring cache 이용하여 캐싱 처리하므로 미사용
 */
@Deprecated(forRemoval = true)
@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRedisCacheRepository implements MemberCacheRepository {

    @Qualifier("memberRedisTemplate")
    private final RedisTemplate<String, Member> redisTemplate;

    @Override
    public Optional<Member> findByEmail(String email) {
        String key = RedisKeyGenerator.getMemberKey(email);
        Member cachedMember = redisTemplate.opsForValue().get(email);
        if (cachedMember != null) {
            log.info("Redis 캐시 히드 : {}", email);
        }
        return Optional.ofNullable(cachedMember);
    }

    @Override
    public void save(Member member) {
        String key = RedisKeyGenerator.getMemberKey(member.getEmail());
        redisTemplate.opsForValue().set(key, member, Duration.ofMinutes(10));
        log.info("Redis 캐시 저장 : {}", member.getEmail());
    }

    @Override
    public void deleteByEmail(String email) {
        String key = RedisKeyGenerator.getMemberKey(email);
        redisTemplate.delete(key);
        log.info("Redis 캐시 삭제 : {}", email);
    }
}
