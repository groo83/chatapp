package com.groo.chatapp.domain.member.service;

import com.groo.chatapp.EmbeddedRedisConfig;
import com.groo.chatapp.domain.member.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(EmbeddedRedisConfig.class)
public class MemberCachePerformanceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private RedisTemplate<String, Member> memberRedisTemplate;

    @Autowired
    private CacheManager cacheManager;

    private static final String CACHE_NAME = "memberCache";
    private static final String  MEMBER_EMAIL = "test";

    void clearCache() {
        // 특정 캐시만 삭제 (memberCache)
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void testPerformanceWithoutCache() {
        clearCache();
        long start = System.nanoTime();
        Member member = memberService.findByEmail(MEMBER_EMAIL);
        long end = System.nanoTime();
        System.out.println("Without Cache: " + (end - start) / 1_000_000.0 + " ms");
        assertNotNull(member);

    }

    @Test
    void testPerformanceWithCache() {
        // 캐시 적용 후 실행
        long start = System.nanoTime();
        Member cachedMember = memberService.findByEmail(MEMBER_EMAIL);
        long end = System.nanoTime();

        System.out.println("With Cache: " + (end - start) / 1_000_000.0 + " ms");
        assertNotNull(cachedMember);
    }
}
