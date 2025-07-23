package com.groo.chatapp.domain.member.service;

import com.groo.chatapp.common.code.ErrorCode;
import com.groo.chatapp.common.exception.EntityNotFoundException;
import com.groo.chatapp.common.redis.RedisCacheService;
import com.groo.chatapp.domain.member.Member;
import com.groo.chatapp.domain.member.dto.MemberRegReqDto;
import com.groo.chatapp.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RedisCacheService redisCacheService;
    private final PasswordEncoder passwordEncoder;

    //private final ApplicationContext applicationContext;

    public Member findByEmail(String email) {
        // proxy 객체 생성해서 호출
        // return this.getSpringProxy().cacheByEmail(memberReqDto.getEmail());
        Member cachedMember = getMemberFromCache(email);
        return cachedMember != null ? cachedMember : findAndCacheMember(email);
    }

    private Member getMemberFromCache(String email) {
        return redisCacheService.getCachedMember(email);
    }

    public Member findAndCacheMember(String email) {
        return memberRepository.findByEmail(email)
                .map(member -> {
                    redisCacheService.updateMemberCache(member); // 값 저장
                    return member;
                })
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public Member saveMemberWithCache(MemberRegReqDto reqDto) {
        log.info("DB 저장 및 캐시 갱신: {}", reqDto.getEmail());

        Member member = memberRepository.save(reqDto.toEntity(passwordEncoder.encode(reqDto.getPassword())));
        redisCacheService.updateMemberCache(member);

        return member;
    }

    public boolean isEmailRegistered(String email) {
        return memberRepository.existsByEmail(email);
    }

    /*
    private AuthService getSpringProxy() {
        return applicationContext.getBean(AuthService.class);
    }


    @Cacheable(value = "memberCache")// key = "#email", condition = "#email != null"
    public Member cacheByEmail(String email) {
        log.info("DB 조회 실행: {}", email);
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
*/
}
