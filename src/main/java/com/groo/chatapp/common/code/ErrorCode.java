package com.groo.chatapp.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    CONTENT_NOT_FOUND(404,"C001", "컨텐츠 정보가 없습니다."),
    USER_NOT_FOUND(404,"C002", "사용자 정보가 없습니다."),
    CHATROOM_NOT_FOUND(404,"C003", "채팅방 정보가 없습니다"),
    EXIST_EMAIL(409,"M001", "이미 이메일이 존재합니다."),
    INVALID_USER(401,"M002", "아이디와 비밀번호가 일치하지 않습니다"),
    INVALID_NICKNAME(401,"M002", "잘못된 닉네임입니다. (특수문자 제외, 3자 이상 20자 이하)"),
    UNAUTHORIZED(401, "A001", "인증되지 않은 사용자입니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}

