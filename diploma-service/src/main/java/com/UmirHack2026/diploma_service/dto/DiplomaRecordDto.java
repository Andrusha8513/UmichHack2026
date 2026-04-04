package com.UmirHack2026.diploma_service.dto;

public record DiplomaRecordDto(
        String name,
        String secondName,
        String surName,
        Integer graduationYear,
        String specialty,
        String diplomaNumber,
        String studentEmail
) {
}
