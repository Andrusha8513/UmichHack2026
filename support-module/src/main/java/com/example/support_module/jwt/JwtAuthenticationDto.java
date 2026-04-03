package com.example.support_module.jwt;

import lombok.Data;

@Data
public class JwtAuthenticationDto {
    public String token;
    private String refreshToken;
}
