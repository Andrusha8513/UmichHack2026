package com.UmirHack2026.diploma_service.service;

import com.UmirHack2026.diploma_service.dto.DiplomaRecordDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {

    public List<DiplomaRecordDto> parseCsv(MultipartFile file) {
        List<DiplomaRecordDto> records = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord csvRecord : csvParser) {
                DiplomaRecordDto dto = new DiplomaRecordDto(
                        csvRecord.get("studentName"),
                        csvRecord.get("studentSecondName"),
                        csvRecord.get("studentSurName"),
                        Integer.parseInt(csvRecord.get("graduationYear")),
                        csvRecord.get("specialty"),
                        csvRecord.get("diplomaNumber"),
                        csvRecord.isMapped("studentEmail") ? csvRecord.get("studentEmail") : null
                );
                records.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге CSV файла: " + e.getMessage());
        }
        return records;
    }
}