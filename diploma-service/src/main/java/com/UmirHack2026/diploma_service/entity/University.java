package com.UmirHack2026.diploma_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "university")
public class University {
    @Id
    private Long universityId;

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
