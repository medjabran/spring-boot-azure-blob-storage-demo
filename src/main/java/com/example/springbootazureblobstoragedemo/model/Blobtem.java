package com.example.springbootazureblobstoragedemo.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Blobtem {

    private String name;
    private Long size;
    private String type;

}
