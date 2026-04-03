package com.user_service.user_service.dto;

import lombok.Data;


@Data
public class UserRegistrationDTO {
    private String name;
    private String secondName;
    private String password;
    private String email;
    private String surName;
}
