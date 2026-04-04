package com.UmirHack2026.diploma_service.dto;

public record DiplomaPublicDto (
        String studentName,
        String studentSecondName,
        String studentSurName,
        String university,
        String specialty,
        Integer graduationYear,
        String diplomaNumber,
        String status
){
}
