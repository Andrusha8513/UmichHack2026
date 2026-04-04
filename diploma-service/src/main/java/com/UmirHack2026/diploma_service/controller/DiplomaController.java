package com.UmirHack2026.diploma_service.controller;

import com.UmirHack2026.diploma_service.entity.Diploma;
import com.UmirHack2026.diploma_service.service.DiplomaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/diplomas")
@RequiredArgsConstructor
public class DiplomaController {

    private final DiplomaService diplomaService;

    /**
     * Эндпоинт для загрузки реестра (CSV)
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDiplomas(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-User-Id", required = false) Long universityId) {

        if (universityId == null) universityId = 1L; // Фоллбэк для тестов без Gateway

        diplomaService.processDiplomaBatchUpload(file, universityId);
        return ResponseEntity.ok(Map.of("message", "Реестр успешно загружен"));
    }

    /**
     * Получение QR-кода диплома в виде PNG картинки.
     * Эту ссылку можно вставлять прямо в <img src="..."> на фронтенде.
     */
    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getDiplomaQrCode(@PathVariable Long id) {
        log.info("Запрос QR-кода для диплома с ID: {}", id);

        byte[] qrCodeImage = diplomaService.getQrCodeImage(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"diploma_qr_" + id + ".png\"")
                .body(qrCodeImage);
    }

    /**
     * Верификация диплома по загруженной фотографии/скриншоту QR-кода.
     * Возвращает полные данные диплома, если он найден.
     */
    @PostMapping("/verify-qr")
    public ResponseEntity<?> verifyByQrFile(@RequestParam("file") MultipartFile file) {
        log.info("Запрос на верификацию диплома через файл: {}", file.getOriginalFilename());

        try {
            Diploma diploma = diplomaService.verifyDiplomaByQrFile(file);
            return ResponseEntity.ok(diploma);
        } catch (RuntimeException e) {
            log.warn("Ошибка верификации: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Критическая ошибка при чтении QR-кода", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Не удалось распознать QR-код"));
        }
    }
}