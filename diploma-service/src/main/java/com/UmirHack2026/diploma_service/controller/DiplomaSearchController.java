package com.UmirHack2026.diploma_service.controller;



import com.UmirHack2026.diploma_service.dto.DiplomaPublicDto;
import com.UmirHack2026.diploma_service.dto.StudentSearchRequestDto;
import com.UmirHack2026.diploma_service.service.DiplomaSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/diplomas/search")
@RequiredArgsConstructor
public class DiplomaSearchController {

    private final DiplomaSearchService diplomaSearchService;

    // Пример: GET http://localhost:8765/api/v1/diplomas/search?diplomaNumber=KZ-983654671
    //Authorization: Bearer {{token}}
    @GetMapping(params = "diplomaNumber")
    public ResponseEntity<List<DiplomaPublicDto>> searchByDiplomaNumber(@RequestParam String diplomaNumber) {
        List<DiplomaPublicDto> result = diplomaSearchService.findByDiplomaNumber(diplomaNumber);
        return ResponseEntity.ok(result);
    }

    // Пример: GET /api/v1/diplomas/search?surName=Иванов&name=Иван&secondName=Иванович
    @GetMapping(params = {"surName", "name"})
    public ResponseEntity<List<DiplomaPublicDto>> searchByFullName(
            @RequestParam String surName,
            @RequestParam String name,
            @RequestParam(required = false) String secondName
    ) {
        StudentSearchRequestDto request = new StudentSearchRequestDto(name, surName, secondName);
        List<DiplomaPublicDto> result = diplomaSearchService.findByFullName(request);
        return ResponseEntity.ok(result);
    }
}
