package com.groo.chatapp.domain.chat.service;

import com.groo.chatapp.common.code.ErrorCode;
import com.groo.chatapp.common.exception.BusinessException;
import com.groo.chatapp.common.util.SecurityUtil;
import com.groo.chatapp.domain.chat.ChatMessage;
import com.groo.chatapp.domain.chat.dto.ChatMessageDto;
import com.groo.chatapp.domain.chat.repository.ChatMessageRepository;
import com.groo.chatapp.domain.member.CustomUserDetails;
import com.groo.chatapp.domain.member.Member;
import com.groo.chatapp.domain.member.dto.MemberDto;
import com.groo.chatapp.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveMessage(ChatMessageDto dto, Authentication authentication) {
        Member member = getMember(authentication);
        ChatMessage message = dto.toEntity(dto, member);
        messageRepository.save(message);
    }

    private Member getMember(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        MemberDto memberDto = MemberDto.from(customUserDetails);

        return memberRepository.findByEmail(memberDto.getEmail()).orElseThrow(() ->
                        new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
