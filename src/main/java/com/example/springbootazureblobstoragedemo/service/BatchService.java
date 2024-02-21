package com.example.springbootazureblobstoragedemo.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.springbootazureblobstoragedemo.model.DataItem;
import com.github.javafaker.Faker;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(prefix = "spring.cloud.azure.storage", name = "blob.enabled", havingValue = "true")
@Service
@AllArgsConstructor
@Slf4j
public class BatchService {

    private final AzureBlobStorageService azureBlobStorageService;

    private final static String DATE_TIME_FORMAT = "dd_MM_yy_HH_mm";

    @Scheduled(cron = "0 */1 * * * ?")
    private void executeBatchOne() {
        log.info("Start Batch ONE : ------------------------------------");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String dateTimeNow = LocalDateTime.now().format(formatter);
        String blobName = "batch/one/results_one_".concat(dateTimeNow).concat(".csv");

        File file = generateCSVFile(generateData(), blobName);

        if (file.exists() && azureBlobStorageService.uploadBlobFromPath(blobName, file.getAbsolutePath())) {
            log.info("File uploaded successfully for blob : {}", blobName);
        }
        log.info("End Batch ONE   : ------------------------------------");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    private void executeBatchTwo() throws IOException {
        log.info("Start Batch TWO : ------------------------------------");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String dateTimeNow = LocalDateTime.now().format(formatter);
        String blobName = "batch/two/results_two_".concat(dateTimeNow).concat(".csv");

        byte[] data = generateCSVByte(generateData());

        if (Objects.nonNull(data) && azureBlobStorageService.uploadBlobFromByte(blobName, data)) {
            log.info("File uploaded successfully for blob : {}", blobName);
        }
        log.info("End Batch TWO   : ------------------------------------");
    }

    @Scheduled(cron = "0 */3 * * * ?")
    private void archiveBatch() throws IOException {
        log.info("Start Archive Batch : ------------------------------------");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String dateTimeNow = LocalDateTime.now().format(formatter);
        String blobName = "archives/results_two_".concat(dateTimeNow).concat(".csv");

        //azureBlobStorageService.listOfBlobs().stream()
                //.map()

        log.info("End Archive Batch   : ------------------------------------");
    }

    private File generateCSVFile(List<DataItem> items, String filePath) {

        File parentDir = new File(filePath).getParentFile();

        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new RuntimeException("Failed to create directories: " + parentDir.getAbsolutePath());
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<DataItem> mappingStrategy = new ColumnPositionMappingStrategy<>();

            mappingStrategy.setType(DataItem.class);

            String[] columns = { "Id", "Value" };
            mappingStrategy.setColumnMapping(columns);

            StatefulBeanToCsv<DataItem> beanWriter =
                    new StatefulBeanToCsvBuilder<DataItem>(writer).withMappingStrategy(mappingStrategy).build();

            beanWriter.write(items);

            return new File(filePath);
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] generateCSVByte(List<DataItem> data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String csvRow = "ID,VALUE\n";
            outputStream.write(csvRow.getBytes(StandardCharsets.UTF_8));
            for (DataItem item : data) {
                csvRow = item.getId() + "," + item.getValue() + "\n";
                outputStream.write(csvRow.getBytes(StandardCharsets.UTF_8));
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.info("Failed to generate CSV data");
        }
        return null;
    }

    private List<DataItem> generateData() {
        ArrayList<DataItem> items = new ArrayList<>();
        Faker faker = new Faker(new Locale("fr-FR"));

        for (int i = 0; i < 100; i++) {
            items.add(DataItem.builder().id(UUID.randomUUID()).value(faker.lorem().characters(30)).build());
        }

        return items;
    }

}
