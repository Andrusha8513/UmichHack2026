package com.UmirHack2026.diploma_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "diplomas",
        indexes = {
                @Index(name = "idx_diploma_number", columnList = "diplomaNumber"),
                @Index(name = "idx_university_id", columnList = "universityId"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_student_name", columnList = "studentName"),
                @Index(name = "idx_student_second_name", columnList = "studentSecondName"),
                @Index(name = "idx_student_sur_name", columnList = "studentSurName")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diploma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "university_id")
    private Long universityId;

    private String studentName;
    private String studentSecondName;
    private String studentSurName;
    private String studentEmail;

    private Integer graduationYear;
    private String specialty;
    private String diplomaNumber;
    private String dataHash;

    @Column(columnDefinition = "TEXT")
    private String signature;

    @Enumerated(EnumType.STRING)
    private DiplomaStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;

    private String universityName;
    private String universitySecondName;
    private String universitySurName;
    private String university;
    private String universityEmail;
}
