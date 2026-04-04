package com.UmirHack2026.diploma_service.service;

import com.UmirHack2026.diploma_service.dto.DiplomaPublicDto;
import com.UmirHack2026.diploma_service.dto.StudentSearchRequestDto;
import com.UmirHack2026.diploma_service.entity.Diploma;
import com.UmirHack2026.diploma_service.mapper.DiplomaMapper;
import com.UmirHack2026.diploma_service.repository.DiplomaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiplomaSearchService {

    private final DiplomaRepository diplomaRepository;
    private final DiplomaMapper diplomaMapper;


    public List<DiplomaPublicDto> findByDiplomaNumber(String diplomaNumber) {
        return diplomaRepository.findByDiplomaNumber(diplomaNumber)
                .map(diplomaMapper::toPublicDto)
                .map(List::of)
                .orElse(Collections.emptyList());
    }

    public List<DiplomaPublicDto> findByFullName(StudentSearchRequestDto searchRequest) {
        String name = searchRequest.name();
        String surName = searchRequest.surName();
        String secondName = searchRequest.secondName();

        List<Diploma> foundDiplomas;

        if (StringUtils.hasText(surName) && StringUtils.hasText(name)) {
            if (StringUtils.hasText(secondName)) {
                foundDiplomas = diplomaRepository.findByStudentSurNameIgnoreCaseAndStudentNameIgnoreCaseAndStudentSecondNameIgnoreCase(
                        surName, name, secondName);
            } else {
                foundDiplomas = diplomaRepository.findByStudentSurNameIgnoreCaseAndStudentNameIgnoreCase(
                        surName, name);
                log.info("Найдено дипломов в БД: {}", foundDiplomas.size());
            }
        } else {

            return Collections.emptyList();
        }

        return foundDiplomas.stream()
                .map(diplomaMapper::toPublicDto)
                .collect(Collectors.toList());
    }
}

