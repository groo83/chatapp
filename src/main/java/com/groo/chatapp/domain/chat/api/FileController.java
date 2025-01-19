package com.groo.chatapp.domain.chat.api;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@RestController
@RequestMapping("/files")
public class FileController {

    private final String uploadDir = "uploads/";

    @PostConstruct
    public void init() {
        File directory = new File(uploadDir);
        if (!directory.exists() && !directory.mkdirs()) {
            log.error("Failed to create directories: {}", directory.getAbsolutePath());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename(); // + "_" + System.currentTimeMillis();

        fileName = fileName.replace(" ", "_");
        log.info("save fileName : {}", fileName);

        Path path = Paths.get(uploadDir, fileName);

        Files.write(path, file.getBytes());
        return ResponseEntity.ok(fileName);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName) throws MalformedURLException, UnsupportedEncodingException {
        Path path = Paths.get(uploadDir, fileName);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found: " + fileName);
        }

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileName, UTF_8) // UTF-8 인코딩 적용
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }
}
