package com.groo.chatapp.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    CONTENT_NOT_FOUND(400,"C001", "컨텐츠 정보가 없습니다."),
    USER_NOT_FOUND(400,"C002", "사용자 정보가 없습니다."),
    INVALID_CHATROOM(404,"C003", "채팅방 정보가 없습니다"),
    EXIST_EMAIL(404,"M001", "이미 이메일이 존재합니다."),
    INVALID_USER(404,"M002", "아이디와 비밀번호가 일치하지 않습니다"),

    ;

    private int status;
    private final String code;
    private final String message;
}

