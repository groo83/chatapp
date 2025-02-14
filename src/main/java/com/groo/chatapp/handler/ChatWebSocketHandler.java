package com.groo.chatapp.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Map;

/**
 * STOMP 프로토콜 사용으로 주석처리 : Controller @MessageMapping, @SendTo 사용
 */
public class ChatWebSocketHandler extends TextWebSocketHandler {
/*
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // JSON String 파싱
        //Map<String, String> payload = objectMapper.readValue(message.getPayload(), Map.class);
        //String type = payload.get("type");// "message" 또는 "file"

        // 메시지 파싱 (JSON 형식으로 파일 또는 메시지를 구분)
        String payload = message.getPayload();

        if (payload.contains("\"type\":\"file\"")) {
            // 파일 처리 로직
            String fileName = payload.split("\"fileName\":\"")[1].split("\"")[0];
            String fileData = payload.split("\"fileData\":\"")[1].split("\"")[0];

            byte[] fileBytes = Base64.getDecoder().decode(fileData);

            File directory = new File("uploads");
            if (!directory.exists()) {
                directory.mkdirs(); // 디렉토리 없으면 생성
            }

            File file = new File(directory, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileBytes); // 파일 저장
            }
            // 클라이언트에 파일 업로드 완료 메세지 전송
            session.sendMessage(new TextMessage("File uploaded successfully: " + fileName));
        } else if (payload.contains("\"type\":\"message\"")) {
            // 일반 메시지 처리
            String messageContent = payload.split("\"content\":\"")[1].split("\"")[0];
            session.sendMessage(new TextMessage("Message: " + messageContent));
        }

    }
    */
}
