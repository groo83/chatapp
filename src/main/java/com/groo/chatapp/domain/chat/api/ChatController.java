package com.groo.chatapp.domain.chat.api;

import com.groo.chatapp.domain.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat") // ws:// STOMP 프로토콜 사용 경로 맵핑 : stompClient.send("/app/chat" , /app prefix 설정 : config 참고
    // @SendTo("/topic/messages") // /topic/messages로 구독한 클라이언트에게 메시지 전송
    public void handleMessage(@Payload ChatMessage message) throws Exception {
        if ("file".equals(message.getType())) {
            message.setFileUrl("/files/download/" + URLEncoder.encode(message.getFileName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20"));
        }

        messagingTemplate.convertAndSend("/topic/messages", message);
    }

    private static void fileEncodeBase64(ChatMessage message) throws IOException {
        // 파일 처리 로직
        String fileName = message.getFileName();
        String fileData = message.getFileData();

        byte[] fileBytes = Base64.getDecoder().decode(fileData);

        File directory = new File("uploads");

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directories: " + directory.getAbsolutePath());
        }

        fileName = fileName.replace(" ", "_");

        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(fileBytes);
        }

        message.setContent("File uploaded successfully: " + fileName);
    }
}
