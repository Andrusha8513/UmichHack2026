package com.example.support_module.jwt;

public class JwtRefreshException extends RuntimeException {
    public JwtRefreshException(String message) {
        super(message);
    }

    public JwtRefreshException(String message  , Throwable error) {
        super(message , error);
    }
}
