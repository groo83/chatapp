package com.groo.chatapp.security.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE) // 인스턴스화 방지
public class SecurityConstants {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String[] PUBLIC_URLS = {
            "/", "/files/**", "/main", "/register", "/api/**", "/home",
            "/css/**", "/js/**", "/ws-stomp/**", "/favicon.ico"
    };

    public static final String[] IGNORE_WHITELIST = {
            "/swagger-ui/**", "/swagger-resources/**", "/v3/controller-docs/**", "/h2-console/**"
    };
}
