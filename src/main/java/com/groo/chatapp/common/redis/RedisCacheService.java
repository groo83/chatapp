package com.groo.chatapp.common.redis;

import com.groo.chatapp.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    @CachePut(value = "memberCache", key = "#result.email")
    public Member updateMemberCache(Member member) {
        return member;
    }

    @Cacheable(value = "memberCache")// key = "#email", condition = "#email != null"
    public Member getCachedMember(String email) {
        log.info("memberCache missed: {}", email);
        return null;
    }

    @CacheEvict(value = "memberCache")
    public void deleteMemberCache(String email) {
        log.info("memberCache 삭제: {}", email);
    }
}
