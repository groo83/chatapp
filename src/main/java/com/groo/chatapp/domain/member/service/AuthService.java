package com.groo.chatapp.domain.member.service;

import com.groo.chatapp.common.code.ErrorCode;
import com.groo.chatapp.common.jwt.TokenDto;
import com.groo.chatapp.common.jwt.TokenProvider;
import com.groo.chatapp.domain.member.Member;
import com.groo.chatapp.domain.member.dto.MemberRegReqDto;
import com.groo.chatapp.domain.member.dto.MemberRegResDto;
import com.groo.chatapp.domain.member.dto.MemberReqDto;
import com.groo.chatapp.domain.member.dto.MemberResDto;
import com.groo.chatapp.domain.member.repository.MemberRepository;
import com.groo.chatapp.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    @Transactional
    public MemberRegResDto signup(MemberRegReqDto reqDto) {
        if (memberRepository.existsByEmail(reqDto.getEmail())) {
            throw new BusinessException(ErrorCode.EXIST_EMAIL);
        }
        Member member = memberRepository.save(reqDto.toEntity(passwordEncoder));

        return MemberRegResDto.fromEntity(member);
    }

    public MemberResDto signin(MemberReqDto memberReqDto) {
        UsernamePasswordAuthenticationToken authenticationToken = memberReqDto.toAuthentication();

        // Spring Security에서 자동으로 비밀번호 검증 진행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        return MemberResDto.from(tokenDto, getMember(memberReqDto));
    }

    private Member getMember(MemberReqDto memberReqDto) {
        return memberRepository.findByEmail(memberReqDto.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
