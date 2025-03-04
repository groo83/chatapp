package com.groo.chatapp.common.valid;

import org.springframework.stereotype.Component;

@Component
public class NicknameValidator {

    private static final String NICKNAME_PATTERN = "^[a-zA-Z0-9가-힣]{3,20}$";

    public boolean isValid(String nickname) {
        return nickname != null && nickname.matches(NICKNAME_PATTERN);
    }
}