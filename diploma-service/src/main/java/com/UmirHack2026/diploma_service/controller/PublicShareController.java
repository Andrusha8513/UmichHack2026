package com.UmirHack2026.diploma_service.controller;

import com.UmirHack2026.diploma_service.barcode.BarcodeService;
import com.UmirHack2026.diploma_service.dto.DiplomaShareViewDto;
import com.UmirHack2026.diploma_service.entity.Diploma;
import com.UmirHack2026.diploma_service.service.DiplomaShareService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/ara/share")
@RequiredArgsConstructor
public class PublicShareController {

    private final DiplomaShareService shareService;
    private final BarcodeService barcodeService;

    @GetMapping("/{token}")
    public String viewDiplomaPage(@PathVariable String token, Model model, HttpServletRequest request) {
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
            model.addAttribute("diploma", view);
            model.addAttribute("qrUrl", diploma.getQrCodeUrl());
            return "diploma-view";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "diploma-view";
        }
    }

    @GetMapping("/qr-by-url")
    public ResponseEntity<byte[]> getQrByUrl(@RequestParam String url) {
        byte[] qrImage = barcodeService.generateCode(url);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrImage);
    }
}