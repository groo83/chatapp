package com.groo.chatapp.domain.member.repository;

import com.groo.chatapp.domain.member.Member;

import java.util.Optional;

/**
 * Cacheable 등 spring cache 이용하여 캐싱 처리하므로 미사용
 */
@Deprecated
public interface MemberCacheRepository {
    Optional<Member> findByEmail(String email);
    void save(Member member);
    void deleteByEmail(String email);
}
