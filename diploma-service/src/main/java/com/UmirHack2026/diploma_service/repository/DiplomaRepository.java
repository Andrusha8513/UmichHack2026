package com.UmirHack2026.diploma_service.repository;


import com.UmirHack2026.diploma_service.entity.Diploma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiplomaRepository extends JpaRepository<Diploma, Long> {
    Optional<Diploma> findByDiplomaNumber(String diplomaNumber);
    boolean existsByDiplomaNumber(String diplomaNumber);

    Optional<Diploma> findByQrCodeUrl(String decodedUrl);

    List<Diploma> findByStudentSurNameIgnoreCaseAndStudentNameIgnoreCaseAndStudentSecondNameIgnoreCase(
            String surName, String name, String secondName);

    List<Diploma> findByStudentSurNameIgnoreCaseAndStudentNameIgnoreCase(
            String surName, String name);


}
