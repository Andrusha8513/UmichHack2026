package com.UmirHack2026.diploma_service.controller;


import com.UmirHack2026.diploma_service.dto.DiplomaShareViewDto;
import com.UmirHack2026.diploma_service.entity.Diploma;
import com.UmirHack2026.diploma_service.service.DiplomaShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ara/share")
@RequiredArgsConstructor
public class PublicShareController {

    private final DiplomaShareService shareService;

    @GetMapping("/{token}")
    public ResponseEntity<?> viewDiplomaByToken(@PathVariable String token) {
        try {
            Diploma diploma = shareService.getDiplomaByShareToken(token);
            DiplomaShareViewDto view = DiplomaShareViewDto.builder()
                    .studentName(diploma.getStudentName())
                    .studentSecondName(diploma.getStudentSecondName())
                    .studentSurName(diploma.getStudentSurName())
                    .specialty(diploma.getSpecialty())
                    .graduationYear(diploma.getGraduationYear())
                    .diplomaNumber(diploma.getDiplomaNumber())
                    .university(diploma.getUniversity())
                    .universityEmail(diploma.getUniversityEmail())
                    .status(diploma.getStatus().name())
                    .issuedAt(diploma.getCreatedAt())
                    .qrCodeUrl(diploma.getQrCodeUrl())
                    .build();
            return ResponseEntity.ok(view);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}