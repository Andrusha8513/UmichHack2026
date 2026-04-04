package com.UmirHack2026.diploma_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class CryptoService {
    @Value("${app.hash.salt}")
    private String globalSalt;

    // Генерируем SHA-256 хеш на основе сырых данных
    public String generateHash(String name , String secondName , String surName, Integer year, String specialty,
                               String diplomaNumber, Long universityId) {
        String salt = globalSalt + "|" + universityId + "|" + diplomaNumber;
        String rawData = String.format("%s|%s|%s|%d|%s|%s|%s", name , secondName , surName, year, specialty, diplomaNumber, salt);
        return DigestUtils.sha256Hex(rawData);
    }

    // Эмуляция цифровой подписи ВУЗа (в реальной жизни тут асимметричное шифрование RSA/GOST)
    public String emulateUniversitySignature(String dataHash, Long universityId) {
        String signaturePayload = dataHash + ":SIGNED_BY_UNI_" + universityId;
        return Base64.getEncoder().encodeToString(signaturePayload.getBytes());
    }
}
