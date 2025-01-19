package com.groo.chatapp.domain.chat.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String sender;
    private String type;   // 메시지 타입 ("message" 또는 "file")
    private String content;  // 메시지 내용
    private String fileName;  // 파일 이름
    private String fileUrl;  // Download URL
    private String fileData;  // base64 인코딩 데이터
}
