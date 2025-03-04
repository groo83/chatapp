package com.groo.chatapp.domain.chat.api;

import com.groo.chatapp.domain.chat.dto.ChatMessageDto;
import com.groo.chatapp.domain.chat.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService messageService;

    @MessageMapping("/chat")
    public void handleMessage(@Payload ChatMessageDto message, Authentication authentication) {
        messageService.handleMessageAndNotifySubscribers(message, authentication);
    }

    private void fileEncodeBase64(ChatMessageDto message) throws IOException {
        String fileName = message.getFileName();
        //String fileData = message.getFileData();

        byte[] fileBytes = Base64.getDecoder().decode("");// fileData

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
