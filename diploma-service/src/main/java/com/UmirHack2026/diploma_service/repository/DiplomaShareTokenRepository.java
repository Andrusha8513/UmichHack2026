package com.UmirHack2026.diploma_service.repository;

import com.UmirHack2026.diploma_service.entity.DiplomaShareToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DiplomaShareTokenRepository extends JpaRepository<DiplomaShareToken, Long> {
    Optional<DiplomaShareToken> findByToken(String token);
    void deleteAllByExpiryDateBefore(LocalDateTime now);
}