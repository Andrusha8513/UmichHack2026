package com.user_service.user_service.dto;

import com.example.support_module.jwt.Role;

public record UniversityRegistrationDto(
        String name,
        String secondName,
        String password,
        String email,
        String surName,
        Role role,
        String university) {
}
