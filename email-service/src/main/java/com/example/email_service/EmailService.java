package com.example.email_service;

import com.example.email_service.dto.EmailRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;



    public void sendConfirmationEmail(EmailRequestDto emailRequestDto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailRequestDto.getTo());
        message.setSubject("Подтверждении регистрации");
        message.setText("Код для регистрации" + emailRequestDto.getCode());
        mailSender.send(message);
    }
    public void sendPasswordResetCode(EmailRequestDto emailRequestDto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailRequestDto.getTo());
        message.setSubject("Код для смены пароля");
        message.setText(emailRequestDto.getCode());
        mailSender.send(message);
    }
}
