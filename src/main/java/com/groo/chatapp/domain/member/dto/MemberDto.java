package com.groo.chatapp.domain.member.dto;

import com.groo.chatapp.domain.member.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 인증정보 바인딩
 */
@Getter
@AllArgsConstructor
public class MemberDto {

    private final Long id;
    private final String email;
    private final String nickname;

    public static MemberDto from(CustomUserDetails userDetails) {
        return new MemberDto(userDetails.getMemberId(), userDetails.getUsername(), userDetails.getNickname());
    }
}
