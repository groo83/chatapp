package com.groo.chatapp.domain.member.dto;

import com.groo.chatapp.common.jwt.TokenDto;
import com.groo.chatapp.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResDto {

    private String nickname;
    private String email;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;

    public static MemberResDto from(TokenDto dto, Member member) {
        return MemberResDto.builder()
                .email(member.getEmail())
                .nickname(member.getNickname())
                .grantType(dto.getGrantType())
                .accessToken(dto.getAccessToken())
                .refreshToken(dto.getRefreshToken())
                .accessTokenExpiresIn(dto.getAccessTokenExpiresIn())
                .build();
    }

    @Builder
    public MemberResDto(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}