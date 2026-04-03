package com.user_service.user_service.exeptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<String> handlerException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Аутентификация не удалась " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handlerGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Аутентификация не удалась " + ex.getMessage());
    }
}
