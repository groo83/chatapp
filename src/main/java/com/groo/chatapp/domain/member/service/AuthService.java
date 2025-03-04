package com.groo.chatapp.domain.member.service;

import com.groo.chatapp.common.code.ErrorCode;
import com.groo.chatapp.common.exception.DuplicateResourceException;
import com.groo.chatapp.security.jwt.TokenDto;
import com.groo.chatapp.security.jwt.TokenProvider;
import com.groo.chatapp.domain.member.Member;
import com.groo.chatapp.domain.member.dto.MemberRegReqDto;
import com.groo.chatapp.domain.member.dto.MemberRegResDto;
import com.groo.chatapp.domain.member.dto.MemberReqDto;
import com.groo.chatapp.domain.member.dto.MemberResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    public MemberRegResDto signup(MemberRegReqDto reqDto) {
        if (memberService.isEmailRegistered(reqDto.getEmail())) {
            throw new DuplicateResourceException(ErrorCode.EXIST_EMAIL);
        }
        Member member = memberService.saveMemberWithCache(reqDto);

        return MemberRegResDto.fromEntity(member);
    }

    public MemberResDto signin(MemberReqDto memberReqDto) {
        UsernamePasswordAuthenticationToken authenticationToken = memberReqDto.toAuthentication();

        // Spring Security에서 자동으로 비밀번호 검증 진행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        return MemberResDto.from(tokenDto, memberService.findByEmail(memberReqDto.getEmail()));
    }

    public MemberResDto noCache_signin(MemberReqDto memberReqDto) {
        UsernamePasswordAuthenticationToken authenticationToken = memberReqDto.toAuthentication();

        // Spring Security에서 자동으로 비밀번호 검증 진행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        return MemberResDto.from(tokenDto, memberService.findAndCacheMember(memberReqDto.getEmail()));
    }
}
