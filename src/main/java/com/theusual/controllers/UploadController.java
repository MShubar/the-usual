package com.theusual.controllers;

import com.theusual.services.AzureStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload-image")
@RequiredArgsConstructor
public class UploadController {
    private final AzureStorageService azureStorageService;

    @PostMapping
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = azureStorageService.uploadFile(file);
        return ResponseEntity.ok(url);
    }
}
