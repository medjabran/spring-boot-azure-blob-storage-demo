package com.example.springbootazureblobstoragedemo.service;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import com.azure.core.util.polling.LongRunningOperationStatus;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobCopyInfo;
import com.example.springbootazureblobstoragedemo.model.Blobtem;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AzureBlobStorageService {

    private final BlobContainerClient blobContainerClient;

    public AzureBlobStorageService(BlobServiceClientBuilder blobServiceClientBuilder, String containerName) {
        this.blobContainerClient =
                requireNonNull(blobServiceClientBuilder, "blobServiceClientBuilder is null").buildClient()
                        .createBlobContainerIfNotExists(containerName).getServiceClient()
                        .getBlobContainerClient(containerName);
    }

    public List<Blobtem> listOfBlobs() {
        return blobContainerClient.listBlobs().stream().map(blobItem -> Blobtem.builder().name(blobItem.getName())
                .size(blobItem.getProperties().getContentLength()).type(blobItem.getProperties().getContentType())
                .build()).collect(Collectors.toList());
    }

    public boolean uploadBlobFromByte(String blobName, byte[] data) {
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            blobContainerClient.getBlobClient(blobName).upload(inputStream, data.length);
            return true;
        } catch (IOException e) {
            log.info("Failed to upload CSV to Azure Blob Storage", e);
        } catch (Exception e) {
            log.error("Exception when upload file in blob storage", e);
        }
        return false;
    }

    public Boolean uploadBlobFromPath(String blobName, String path) {
        try {
            blobContainerClient.getBlobClient(blobName).uploadFromFile(path, true);
            return true;
        } catch (Exception e) {
            log.error("Exception when upload file in blob storage", e);
        }
        return false;
    }

    public boolean copyBlob(String fromBlobName, String toBlobName) {
        BlobClient sourceBlobClient = blobContainerClient.getBlobClient(fromBlobName);
        BlobClient destBlobClient = blobContainerClient.getBlobClient(toBlobName);
        SyncPoller<BlobCopyInfo, Void> poller =
                destBlobClient.beginCopy(sourceBlobClient.getBlobUrl(), Duration.ofSeconds(10));
        PollResponse<BlobCopyInfo> response =
                poller.waitForCompletion(Duration.ofSeconds(2, ChronoUnit.MINUTES.ordinal()));

        return response.getStatus().isComplete() && LongRunningOperationStatus.SUCCESSFULLY_COMPLETED.equals(
                response.getStatus());
    }

    public InputStream downloadBlob(String blobName) {
        return blobContainerClient.getBlobClient(blobName).downloadContent().toStream();
    }

    public void deleteBlobIfExists(String blobName) {
        blobContainerClient.getBlobClient(blobName).deleteIfExists();
    }

    public void deleteBlob(String blobName) {
        blobContainerClient.getBlobClient(blobName).delete();
    }

}
