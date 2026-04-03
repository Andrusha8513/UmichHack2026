package com.example.profile_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PrivetUserProfileDto {
    private Long id;
    private String name;
    private String secondName;
    private String surName;
    private String email;
    private Long avatarId;
}
