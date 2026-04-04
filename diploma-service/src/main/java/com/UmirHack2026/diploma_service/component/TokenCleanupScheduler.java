package com.UmirHack2026.diploma_service.component;

import com.UmirHack2026.diploma_service.service.DiplomaShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final DiplomaShareService diplomaShareService;


    @Scheduled(cron = "0 0 3 * * *")
    public void cleanExpiredTokens() {
        log.info("Запущена очистка просроченных токенов");
        diplomaShareService.cleanExpiredTokens();
    }
}
