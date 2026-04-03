package com.example.email_service.dto;

import lombok.Data;

@Data
public class EmailRequestDto {
    private String to;
    private String code;
    private EmailType type;

    public enum EmailType{
        CONFIRMATION,
        PASSWORD_RESET
    }
}
