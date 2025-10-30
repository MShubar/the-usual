package com.theusual.services;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AzureStorageService {

    @Value("${AZURE_STORAGE_CONNECTION_STRING:}")
    private String connectionString;

    @Value("${AZURE_STORAGE_CONTAINER_NAME:coffee-shop}")
    private String containerName;

    public String uploadFile(MultipartFile file) throws IOException {
        BlobContainerClient containerClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient()
                .getBlobContainerClient(containerName);

        String blobName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(blobName);

        blobClient.upload(file.getInputStream(), file.getSize(), true);
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(file.getContentType()));

        return blobClient.getBlobUrl();
    }
}
