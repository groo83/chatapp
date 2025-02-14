package com.groo.chatapp.domain.chat.api;

import com.groo.chatapp.domain.chat.dto.ChatMessageDto;
import com.groo.chatapp.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService messageService;


    @MessageMapping("/chat")
    public void handleMessage(@Payload ChatMessageDto message, Authentication authentication) {
        if (isFileMessage(message)) {
            String fileUrl = generateFileUrl(message.getFileName());
            message.setFileUrl(fileUrl);
        }

        sendMessageToSubscribers(message);
        // todo redis에 저장
        saveMessage(message, authentication);
    }


    private boolean isFileMessage(ChatMessageDto message) {
        return "file".equals(message.getType());
    }

    private String generateFileUrl(String fileName) {
        return "/files/download/" + encodeFileName(fileName);
    }

    private String encodeFileName(String fileName) {
        return URLEncoder.encode(fileName, UTF_8).replaceAll("\\+", "%20");
    }

    private void sendMessageToSubscribers(ChatMessageDto message) {
        messagingTemplate.convertAndSend("/topic/"+ message.getRoomId() + "/messages", message);
    }

    private void fileEncodeBase64(ChatMessageDto message) throws IOException {
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

    private void saveMessage(ChatMessageDto message, Authentication authentication) {
        messageService.saveMessage(message, authentication);
    }
}
