package com.groo.chatapp.common.jwt;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TokenDto {

    private String grantType;

    private String accessToken;

    private String refreshToken;

    private Long accessTokenExpiresIn;
}
