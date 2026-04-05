package com.UmirHack2026.diploma_service.service;

import com.UmirHack2026.diploma_service.entity.Diploma;
import com.UmirHack2026.diploma_service.entity.DiplomaShareToken;
import com.UmirHack2026.diploma_service.repository.DiplomaRepository;
import com.UmirHack2026.diploma_service.repository.DiplomaShareTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiplomaShareService {

    private final DiplomaShareTokenRepository tokenRepository;
    private final DiplomaRepository diplomaRepository;

    @Value("${app.share-token.expiration-hours:168}")
    private long expirationHours;

    @Value("${app.share.base-url}")
    private String baseUrl;


    @Transactional
    public String generateShareLink(Long diplomaId) {
        Diploma diploma = diplomaRepository.findById(diplomaId)
                .orElseThrow(() -> new IllegalArgumentException("Diploma not found with id: " + diplomaId));

        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(expirationHours);

        DiplomaShareToken shareToken = new DiplomaShareToken();
        shareToken.setToken(token);
        shareToken.setDiplomaId(diplomaId);
        shareToken.setExpiryDate(expiry);
        shareToken.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(shareToken);


        return baseUrl + "/" + token;
    }


    @Transactional(readOnly = true)
    public Diploma getDiplomaByShareToken(String token) {
        DiplomaShareToken shareToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Недействительная ссылка"));

        if (shareToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Срок действия ссылки истёк");
        }

        return diplomaRepository.findById(shareToken.getDiplomaId())
                .orElseThrow(() -> new RuntimeException("Диплом не найден"));
    }


    @Transactional
    public void cleanExpiredTokens() {
        tokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
        log.info("Удалены просроченные токены");
    }
}
