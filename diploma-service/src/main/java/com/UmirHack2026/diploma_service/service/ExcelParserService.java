package com.UmirHack2026.diploma_service.service;

import com.UmirHack2026.diploma_service.dto.DiplomaRecordDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelParserService {

    public List<DiplomaRecordDto> parseExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(file, inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (!rows.hasNext()) {
                throw new RuntimeException("Excel файл пуст");
            }


            Row headerRow = rows.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            List<DiplomaRecordDto> records = new ArrayList<>();
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                DiplomaRecordDto dto = mapRowToDto(currentRow, headers);
                if (dto != null) {
                    records.add(dto);
                }
            }
            return records;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге Excel файла: " + e.getMessage(), e);
        }
    }

    private Workbook createWorkbook(MultipartFile file, InputStream inputStream) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new RuntimeException("Имя файла отсутствует");
        }
        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        } else {
            throw new RuntimeException("Неподдерживаемый формат Excel: " + fileName);
        }
    }

    private DiplomaRecordDto mapRowToDto(Row row, List<String> headers) {
        try {
            String studentName = getCellValueAsString(row, headers.indexOf("studentName"));
            String studentSecondName = getCellValueAsString(row, headers.indexOf("studentSecondName"));
            String studentSurName = getCellValueAsString(row, headers.indexOf("studentSurName"));
            Integer graduationYear = getCellValueAsInteger(row, headers.indexOf("graduationYear"));
            String specialty = getCellValueAsString(row, headers.indexOf("specialty"));
            String diplomaNumber = getCellValueAsString(row, headers.indexOf("diplomaNumber"));
            String studentEmail = getCellValueAsString(row, headers.indexOf("studentEmail"));

            return new DiplomaRecordDto(
                    studentName,
                    studentSecondName,
                    studentSurName,
                    graduationYear,
                    specialty,
                    diplomaNumber,
                    studentEmail
            );
        } catch (Exception e) {
            System.err.println("Ошибка в строке " + (row.getRowNum() + 1) + ": " + e.getMessage());
            return null;
        }
    }

    private String getCellValueAsString(Row row, int cellIndex) {
        if (cellIndex < 0) return null;
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }

    private Integer getCellValueAsInteger(Row row, int cellIndex) {
        if (cellIndex < 0) return null;
        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
