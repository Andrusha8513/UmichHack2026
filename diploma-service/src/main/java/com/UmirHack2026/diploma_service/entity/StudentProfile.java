package com.UmirHack2026.diploma_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "studentProfile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfile {
    @Id
    private Long id;

    @Size(min = 1, max = 60, message = "Чё за имя такое длиннее 60 символов?")
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;

    @Size(min = 1, max = 60, message = "Чё за имя такое длиннее 60 символов?")
    @NotBlank(message = "Фамилия не может быть пустой!")
    private String secondName;

    @Size(min = 1, max = 60, message = "Чё за имя такое длиннее 60 символов?")
    @NotBlank(message = "Отчество не может быть пустой!")
    private String surName;

    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не может быть длиннее 100 символов")
    @Column(length = 100)
    private String studentEmail;
}
