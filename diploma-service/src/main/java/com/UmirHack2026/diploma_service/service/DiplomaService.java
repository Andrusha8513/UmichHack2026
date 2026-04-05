package com.UmirHack2026.diploma_service.service;

import com.UmirHack2026.diploma_service.barcode.BarcodeDecoderService;
import com.UmirHack2026.diploma_service.barcode.BarcodeService;
import com.UmirHack2026.diploma_service.dto.DiplomaRecordDto;
import com.UmirHack2026.diploma_service.entity.Diploma;
import com.UmirHack2026.diploma_service.entity.DiplomaStatus;
import com.UmirHack2026.diploma_service.entity.University;
import com.UmirHack2026.diploma_service.repository.DiplomaRepository;
import com.UmirHack2026.diploma_service.repository.UniversityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiplomaService {

    private final DiplomaRepository diplomaRepository;
    private final CsvParserService csvParserService;
    private final CryptoService cryptoService;
    private final ExcelParserService excelParserService;
    private final BarcodeService barcodeService;
    private final BarcodeDecoderService barcodeDecoderService;
    private final UniversityRepository universityRepository;
    private final DiplomaShareService shareService;

    private List<DiplomaRecordDto> parseFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new RuntimeException("Имя файла отсутствует");
        if (fileName.endsWith(".csv")) {
            return csvParserService.parseCsv(file);
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return excelParserService.parseExcel(file);
        } else {
            throw new RuntimeException("Неподдерживаемый формат файла. Используйте CSV или Excel");
        }
    }


    @Transactional
    public void processDiplomaBatchUpload(MultipartFile file, Long universityId) {
        log.info("Начало обработки файла для университета с ID: {}", universityId);

        University university = universityRepository.findById(universityId)
                .orElseThrow(() -> new RuntimeException("Университет с ID " + universityId + " не найден в базе данных"));

        List<DiplomaRecordDto> parsedRecords = parseFile(file);
        List<Diploma> diplomasToSave = new ArrayList<>();

        for (DiplomaRecordDto record : parsedRecords) {
            if (diplomaRepository.existsByDiplomaNumber(record.diplomaNumber())) {
                continue;
            }

            String hash = cryptoService.generateHash(
                    record.name(),
                    record.secondName(),
                    record.surName(),
                    record.graduationYear(),
                    record.specialty(),
                    record.diplomaNumber(),
                    universityId
            );

            String signature = cryptoService.emulateUniversitySignature(hash, universityId);

            Diploma diploma = Diploma.builder()
                    .studentName(record.name())
                    .studentSecondName(record.secondName())
                    .studentSurName(record.surName())
                    .studentEmail(record.studentEmail())
                    .graduationYear(record.graduationYear())
                    .specialty(record.specialty())
                    .diplomaNumber(record.diplomaNumber())
                    .universityId(university.getUniversityId())
                    .universityName(university.getUniversityName())
                    .universitySecondName(university.getUniversitySecondName())
                    .universitySurName(university.getUniversitySurName())
                    .universityEmail(university.getUniversityEmail())
                    .university(university.getUniversity())   // если поле есть
                    .dataHash(hash)
                    .signature(signature)
                    .status(DiplomaStatus.ACTIVE)
                    .qrCodeUrl(null)
                    .build();

            diplomasToSave.add(diploma);
        }


        List<Diploma> savedDiplomas = diplomaRepository.saveAll(diplomasToSave);
        log.info("Сохранено {} дипломов, генерируем временные ссылки", savedDiplomas.size());

        // генерю ссылки через сервис шеринга и обнов qrCodeUrl
        for (Diploma diploma : savedDiplomas) {
            String shareLink = shareService.generateShareLink(diploma.getId());
            diploma.setQrCodeUrl(shareLink);
        }

        // Сохраняем обновлённые дипломы с qrCodeUrl
        diplomaRepository.saveAll(savedDiplomas);
        log.info("Временные ссылки сгенерированы для {} дипломов", savedDiplomas.size());
    }
    public byte[] getQrCodeImage(Long diplomaId) {
        Diploma diploma = diplomaRepository.findById(diplomaId)
                .orElseThrow(() -> new RuntimeException("Диплом не найден"));

        return barcodeService.generateCode(diploma.getQrCodeUrl());
    }

    public Diploma verifyDiplomaByQrFile(MultipartFile qrFile) {
        String decodedUrl = barcodeDecoderService.decoderBarcode(qrFile);
        log.info("Распознан URL из QR: {}", decodedUrl);

        return diplomaRepository.findByQrCodeUrl(decodedUrl)
                .orElseThrow(() -> new RuntimeException("Диплом с таким QR-кодом не зарегистрирован в системе"));
    }


    @Transactional
    public Diploma changeDiplomaStatus(Long diplomaId, DiplomaStatus newStatus, Long universityId) {
        Diploma diploma = diplomaRepository.findById(diplomaId)
                .orElseThrow(() -> new RuntimeException("Диплом не найден"));


        if (!diploma.getUniversityId().equals(universityId)) {
            throw new RuntimeException("У вас нет прав на изменение этого диплома");
        }

        diploma.setStatus(newStatus);
        if (newStatus == DiplomaStatus.REVOKED) {
            diploma.setRevokedAt(LocalDateTime.now());
        } else {
            diploma.setRevokedAt(null); // если снова активируем
        }

        return diplomaRepository.save(diploma);
    }
}
