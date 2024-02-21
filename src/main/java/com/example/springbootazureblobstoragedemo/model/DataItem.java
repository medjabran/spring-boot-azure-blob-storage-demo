package com.example.springbootazureblobstoragedemo.model;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataItem {

    private UUID id;
    private String value;

}
