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

    /**
     * Генерирует временную ссылку для диплома.
     * @param diplomaId ID диплома
     * @return полная URL-ссылка для отправки работодателю
     */
    @Transactional
    public String generateShareLink(Long diplomaId) {
        // проверяем существование диплома
        Diploma diploma = diplomaRepository.findById(diplomaId)
                .orElseThrow(() -> new IllegalArgumentException("Diploma not found with id: " + diplomaId));

        // создаём уникальный токен
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(expirationHours);

        DiplomaShareToken shareToken = new DiplomaShareToken();
        shareToken.setToken(token);
        shareToken.setDiplomaId(diplomaId);
        shareToken.setExpiryDate(expiry);
        shareToken.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(shareToken);

        // формируем полную ссылку (базовый URL можно вынести в конфиг)
//        String baseUrl = "https://your-domain.com/api/ara/share";
        return baseUrl + "/" + token;
    }

    /**
     * Получение диплома по токену с проверкой срока действия.
     * @param token уникальный токен
     * @return Diploma (или DTO) если токен валиден
     * @throws RuntimeException если токен не найден или просрочен
     */
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

    /**
     * Периодическая очистка просроченных токенов (можно вызвать по расписанию).
     */
    @Transactional
    public void cleanExpiredTokens() {
        tokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
        log.info("Удалены просроченные токены");
    }
}
