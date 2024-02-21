package com.example.springbootazureblobstoragedemo.configuration;

import static com.azure.storage.blob.BlobContainerClient.ROOT_CONTAINER_NAME;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.azure.storage.blob.BlobServiceClientBuilder;
import com.example.springbootazureblobstoragedemo.service.AzureBlobStorageService;

@Configuration
public class AzureConfiguration {

    private final static String CONTAINER_NAME = "blob-name.batch";

    @Bean
    AzureBlobStorageService azureBlobStorageClient(BlobServiceClientBuilder blobServiceClientBuilder,
            Environment environment) {
        String containerName = Objects.requireNonNull(environment.getProperty(CONTAINER_NAME)).isBlank() ?
                ROOT_CONTAINER_NAME :
                environment.getProperty(CONTAINER_NAME);
        return new AzureBlobStorageService(blobServiceClientBuilder, containerName);
    }

}
