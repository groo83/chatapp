package com.groo.chatapp.domain.member.dto;


import com.groo.chatapp.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegResDto {

    private String email;
    private String nickname;

    public static MemberRegResDto fromEntity(Member member) {
        return MemberRegResDto.builder()
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
