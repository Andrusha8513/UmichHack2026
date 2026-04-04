package com.UmirHack2026.diploma_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "diplomas",
        indexes = {
                @Index(name = "idx_diploma_number", columnList = "diplomaNumber"),
                @Index(name = "idx_university_id", columnList = "universityId"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_name", columnList = "name"),
                @Index(name = "idx_name", columnList = "secondName"),
                @Index(name = "idx_name", columnList = "surName")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diploma {

    @Id
//    @NotNull(message = "ID университета обязателен")
    private Long id;


    //    @Size(min = 1, max = 60, message = "Чё за имя такое длиннее 60 символов?")
//    @NotBlank(message = "Имя не может быть пустым!")
    private String studentName;

    //    @Size(min = 1, max = 60, message = "Чё за имя такое длиннее 60 символов?")
//    @NotBlank(message = "Фамилия не может быть пустой!")
    private String studentSecondName;

    //    @Size(min = 1, max = 60, message = "Чё за имя такое длиннее 60 символов?")
//    @NotBlank(message = "Отчество не может быть пустой!")
    private String studentSurName;


//private String studentEmail;

    //    @NotNull(message = "Год выпуска обязателен")
//    @Min(value = 1900, message = "Год выпуска не может быть меньше 1900")
//    @Max(value = 2100, message = "Год выпуска не может быть больше 2100")
//    @Column(nullable = false)
    private Integer graduationYear;


    //    @NotBlank(message = "Специальность не может быть пустой")
//    @Size(max = 255, message = "Специальность не может быть длиннее 255 символов")
//    @Column(nullable = false, length = 255)
    private String specialty;


    //    @NotBlank(message = "Номер диплома не может быть пустым")
//    @Size(max = 50, message = "Номер диплома не может быть длиннее 50 символов")
//    @Column(nullable = false, unique = true, length = 50)
    private String diplomaNumber;


    //    @NotBlank(message = "Хеш записи не может быть пустым")
//    @Size(min = 64, max = 64, message = "Хеш должен быть длиной 64 символа (SHA-256)")
//    @Column(nullable = false, unique = true, length = 64)
    private String dataHash;


    //    @NotBlank(message = "Цифровая подпись не может быть пустой")
//    @Column(nullable = false, columnDefinition = "TEXT")
    private String signature;


    //    @NotNull(message = "Статус обязателен")
    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 20)
    private DiplomaStatus status;

    //  Временные метки
    @CreationTimestamp
//    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;


    //    @Size(max = 500, message = "URL QR-кода не может быть длиннее 500 символов")
//    @Column(name = "qr_code_url", length = 500)
    private String qrCodeUrl;


    //    @Size(min = 1, max = 60, message = "Чё за имя такое длиннее 60 символов?")
//    @NotBlank(message = "Имя не может быть пустым!")
    private String universityName;

    //    @Size(min = 1, max = 60, message = "Чё за имя такое длиннее 60 символов?")
//    @NotBlank(message = "Фамилия не может быть пустой!")
    private String universitySecondName;

    //    @Size(min = 1, max = 60, message = "Чё за имя такое длиннее 60 символов?")
//    @NotBlank(message = "Отчество не может быть пустой!")
    private String universitySurName;


    private String university;

    private String universityEmail;
}