package com.user_service.user_service;

import com.example.support_module.jwt.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Size(max = 100 , message = "Почта не может быть длиннее 100 символов!")
    @NotBlank(message = "Почта не может быть пустой!")
    private String email;

    @Size(min = 8 ,max = 100  , message = "Пароль должен быть не короче 8 и не длиннее 100 символов!")
    @NotBlank(message = "Пароль не может быть пустым!")
    private String password;

    @Size(min = 1 ,max = 60 , message = "Чё за имя такое длиннее 60 символов?")
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;

    @Size(min = 1 , max = 60 , message = "Чё за имя такое длиннее 60 символов?")
    @NotBlank(message = "Фамилия не может быть пустой!")
    private String secondName;

    @Size(min = 1 , max = 60 , message = "Чё за имя такое длиннее 60 символов?")
    @NotBlank(message = "Отчество не может быть пустой!")
    private String surName;

    @Size(max = 100 , message = "Почта не может быть длиннее 100 символов!")
    @NotBlank(message = "Почта не может быть пустой!")
    private String pendingEmail;


    private String confirmationCode;
    private Boolean enable = false;
    private String emailChangeCode;
    private String passwordResetCode;
    private LocalDateTime ttlEmailCode;
    private LocalDateTime passwordResetCodeExpiryDate;
    private boolean isAccountNonLocked = false;
    private String refreshToken;


    @ElementCollection(targetClass = Role.class , fetch = FetchType.EAGER)
    @CollectionTable(name = "users_role" , joinColumns = @JoinColumn(name = "users_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

}
