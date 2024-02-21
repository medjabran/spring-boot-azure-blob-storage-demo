package com.example.springbootazureblobstoragedemo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootazureblobstoragedemo.model.Blobtem;
import com.example.springbootazureblobstoragedemo.service.AzureBlobStorageService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class BatchController {

    private final AzureBlobStorageService azureBlobStorageService;

    @GetMapping("list-storage-files")
    public List<Blobtem> getListStorageFiles() {
        return azureBlobStorageService.listOfBlobs();
    }

}
