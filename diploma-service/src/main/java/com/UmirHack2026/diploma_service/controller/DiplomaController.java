package com.UmirHack2026.diploma_service.controller;

import com.UmirHack2026.diploma_service.barcode.BarcodeService;
import com.UmirHack2026.diploma_service.entity.Diploma;
import com.UmirHack2026.diploma_service.entity.DiplomaStatus;
import com.UmirHack2026.diploma_service.service.DiplomaService;
import com.UmirHack2026.diploma_service.service.DiplomaShareService;
import com.example.support_module.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/diplomas")
@RequiredArgsConstructor
public class DiplomaController {

    private final DiplomaService diplomaService;
    private final DiplomaShareService shareService;
    private final BarcodeService barcodeService;


    @PostMapping("/batch-upload/{universityId}")
    public ResponseEntity<String> uploadDiplomas(
            @RequestParam("file") MultipartFile file,
            @PathVariable("universityId") Long universityId

    ) {

     try {
         diplomaService.processDiplomaBatchUpload(file, universityId);
         return ResponseEntity.ok().build();
     }catch (RuntimeException e){
         log.error("Критическая ошибка " + e);
         return ResponseEntity.badRequest().body(e.getMessage());
     }
    }



    //Получение QR-кода диплома в виде PNG картинки.
    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getDiplomaQrCode(@PathVariable Long id) {
        log.info("Запрос QR-кода для диплома с ID: {}", id);

        byte[] qrCodeImage = diplomaService.getQrCodeImage(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"diploma_qr_" + id + ".png\"")
                .body(qrCodeImage);
    }

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

    @GetMapping("/{diplomaId}/qr-share")
    public ResponseEntity<byte[]> getQrCodeWithShareLink(@PathVariable Long diplomaId) {
        String shareLink = shareService.generateShareLink(diplomaId);
        byte[] qrImage = barcodeService.generateCode(shareLink);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(qrImage);
    }

    @PutMapping("/{diplomaId}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long diplomaId,
            @RequestParam DiplomaStatus status,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            Long universityId = customUserDetails.getId();
            Diploma updated = diplomaService.changeDiplomaStatus(diplomaId, status, universityId);
            return ResponseEntity.ok(Map.of("message", "Статус изменён", "status", updated.getStatus()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}