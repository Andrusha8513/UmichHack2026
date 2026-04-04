package com.UmirHack2026.diploma_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DiplomaShareViewDto {
    private String studentName;
    private String studentSecondName;
    private String studentSurName;
    private String specialty;
    private Integer graduationYear;
    private String diplomaNumber;
    private String university;
    private String universityEmail;
    private String status;          // например, "VERIFIED" или "REVOKED"
    private LocalDateTime issuedAt; // дата выдачи (createdAt)
    private String qrCodeUrl;
}