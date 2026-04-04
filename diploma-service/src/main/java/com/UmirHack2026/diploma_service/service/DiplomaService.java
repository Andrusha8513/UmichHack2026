package com.UmirHack2026.diploma_service.service;

import com.UmirHack2026.diploma_service.barcode.BarcodeDecoderService;
import com.UmirHack2026.diploma_service.barcode.BarcodeService;
import com.UmirHack2026.diploma_service.dto.DiplomaRecordDto;
import com.UmirHack2026.diploma_service.entity.Diploma;
import com.UmirHack2026.diploma_service.entity.DiplomaStatus;
import com.UmirHack2026.diploma_service.repository.DiplomaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    // Внедряем сервисы для работы с QR
    private final BarcodeService barcodeService;
    private final BarcodeDecoderService barcodeDecoderService;

    @Transactional
    public void processDiplomaBatchUpload(MultipartFile file, Long universityId) {
        log.info("Начало обработки файла для университета с ID: {}", universityId);

        List<DiplomaRecordDto> parsedRecords = csvParserService.parseCsv(file);
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

            String token = UUID.randomUUID().toString();
            String verificationUrl = "https://umir-hack.ru/verify/" + token;

            Diploma diploma = Diploma.builder()
                    .graduationYear(record.graduationYear())
                    .specialty(record.specialty())
                    .diplomaNumber(record.diplomaNumber())
//                    .universityId(universityId)
                    .studentName(record.name())
//                    .studentEmail(record.studentEmail())
                    .dataHash(hash)
                    .signature(signature)
                    .status(DiplomaStatus.ACTIVE)
                    .qrCodeUrl(verificationUrl)
                    .build();

            diplomasToSave.add(diploma);
        }

        diplomaRepository.saveAll(diplomasToSave);
    }

    /**
     * Метод для генерации картинки QR-кода по ID диплома.
     * Используется контроллером для отдачи PNG пользователю.
     */
    public byte[] getQrCodeImage(Long diplomaId) {
        Diploma diploma = diplomaRepository.findById(diplomaId)
                .orElseThrow(() -> new RuntimeException("Диплом не найден"));

        // BarcodeService превращает сохраненную ссылку в QR-код (byte[])
        return barcodeService.generateCode(diploma.getQrCodeUrl());
    }

    /**
     * Метод верификации диплома через загрузку файла с QR-кодом.
     * Используется работодателем.
     */
    public Diploma verifyDiplomaByQrFile(MultipartFile qrFile) {
        // 1. Декодируем текст из картинки с помощью BarcodeDecoderService
        String decodedUrl = barcodeDecoderService.decoderBarcode(qrFile);
        log.info("Распознан URL из QR: {}", decodedUrl);

        // 2. Ищем диплом в базе по ссылке
        return diplomaRepository.findByQrCodeUrl(decodedUrl)
                .orElseThrow(() -> new RuntimeException("Диплом с таким QR-кодом не зарегистрирован в системе"));
    }
}