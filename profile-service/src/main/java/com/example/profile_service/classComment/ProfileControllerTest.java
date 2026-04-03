//package com.example.profile_service.controller;
//import com.example.profile_service.dto.FullPrivetProfileDto;
//import com.example.profile_service.dto.FullPublicProfileDto;
//import com.example.profile_service.ProfileService;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//@RequiredArgsConstructor
//@RestController
//@RequestMapping("api/profile")
//public class ProfileController {
//
//    private final ProfileService profileService;
//
//    @GetMapping("/my-profile/{id}")
//    public ResponseEntity<FullPrivetProfileDto> getMyProfile(@PathVariable Long id) {
//        try {
//            FullPrivetProfileDto fullProfileDto = profileService.getMyProfile(id);
//            return ResponseEntity.ok(fullProfileDto);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//@GetMapping("/findProfile")
//    public ResponseEntity<FullPublicProfileDto> findProfile(@RequestParam String email){
//        try {
//            FullPublicProfileDto profileDto = profileService.getPublicProfile(email);
//            return ResponseEntity.ok(profileDto);
//        }catch (RuntimeException e){
//            return ResponseEntity.badRequest().build();
//        }
//}
//}
