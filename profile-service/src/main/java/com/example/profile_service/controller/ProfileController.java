package com.example.profile_service.controller;

import com.example.profile_service.dto.PrivetUserProfileDto;

import com.example.profile_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/myProfile/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<PrivetUserProfileDto> getMyProfile(@PathVariable Long id){
        try {
            PrivetUserProfileDto profile =  profileService.getProfile(id);
            return ResponseEntity.ok(profile);
        }catch (Exception e){
            log.info("Причина ошибки " + e);
            return ResponseEntity.badRequest().build();
        }
    }



    @PostMapping("/setAvatar/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> setAvatar(@PathVariable Long id,
                                               @RequestPart("file") MultipartFile file) {
        try {
            profileService.setAvatar(id, file);
            return ResponseEntity.ok("Аватарка успешно обновлена");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/deleteAvatar/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteAvatar(@PathVariable Long id) {
        try {
            profileService.deleteAvatar(id);
            return ResponseEntity.ok("Фото успешно удалены");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
