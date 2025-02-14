package com.groo.chatapp.domain.chat.api;

import com.groo.chatapp.common.exception.FileNotFoundException;
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

        fileName = processFileName(fileName);

        Path path = Paths.get(uploadDir, fileName);
        Files.write(path, file.getBytes());

        return ResponseEntity.ok(fileName);
    }

    private static String processFileName(String fileName) {
        return fileName.replace(" ", "_");
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName) throws MalformedURLException {
        Resource resource = loadFileAsResource(fileName);

        ContentDisposition contentDisposition = createContentDisposition(fileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }

    private static ContentDisposition createContentDisposition(String fileName) {
        return ContentDisposition.builder("attachment")
                .filename(fileName, UTF_8)
                .build();
    }

    private Resource loadFileAsResource(String fileName) throws MalformedURLException {
        Path path = Paths.get(uploadDir, fileName);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("File not found: " + fileName);
        }
        return resource;
    }
}
