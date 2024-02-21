package com.example.springbootazureblobstoragedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringBootAzureBlobStorageDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAzureBlobStorageDemoApplication.class, args);
	}

}
