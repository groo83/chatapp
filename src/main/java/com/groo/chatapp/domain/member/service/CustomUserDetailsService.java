package com.groo.chatapp.domain.member.service;

import com.groo.chatapp.common.code.ErrorCode;
import com.groo.chatapp.common.exception.EntityNotFoundException;
import com.groo.chatapp.domain.member.CustomUserDetails;
import com.groo.chatapp.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new CustomUserDetails(memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND)));
    }
}
