package com.example.email_service;

import com.example.email_service.dto.EmailRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/code")
    public ResponseEntity<?> sendConfirmationCode(@RequestBody EmailRequestDto emailRequestDto) {
        try {
            emailService.sendConfirmationEmail(emailRequestDto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resetPassCode")
    public ResponseEntity<?> sendPasswordResetCode(@RequestBody EmailRequestDto emailRequestDto){
        try {
            emailService.sendPasswordResetCode(emailRequestDto);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
