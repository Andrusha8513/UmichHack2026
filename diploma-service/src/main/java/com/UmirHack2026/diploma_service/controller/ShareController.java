package com.UmirHack2026.diploma_service.controller;

import com.UmirHack2026.diploma_service.service.DiplomaShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareController {

    private final DiplomaShareService shareService;

    @PostMapping("/{diplomaId}")
    public ResponseEntity<?> generateShareLink(@PathVariable Long diplomaId) {
        try {
            String shareLink = shareService.generateShareLink(diplomaId);
            return ResponseEntity.ok(Map.of("shareLink", shareLink));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
