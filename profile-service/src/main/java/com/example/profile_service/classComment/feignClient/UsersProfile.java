//package com.example.profile_service.feignClient;
//
//import com.example.user_service.dto.PrivetUserProfileDto;
//import com.example.user_service.dto.PublicUserProfileDto;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.util.List;
//
//@FeignClient(name =  "users",
//        configuration = FeignMultipartConfig.class)
//public interface UsersProfile {
//
//    @GetMapping(path = "/api/users/me/{id}")
//    PrivetUserProfileDto getMyProfile(@PathVariable Long id);
//
//    @GetMapping("/api/users/profile")
//    PublicUserProfileDto findProfile(@RequestParam String email);
//
//    @PostMapping("/api/users/addAvatar/{id}/newAvatar")
//    String addAvatar(@PathVariable Long id, @RequestPart MultipartFile file);
//
//    @PostMapping("/api/users/addPhotos/{id}")
//    String addPhotos(@PathVariable Long id, @RequestPart List<MultipartFile> files);
//
//    @PutMapping("/api/users/updateAvatar/{id}/{newAvatar}")
//    String updateAvatar(@PathVariable Long id , @PathVariable Long newAvatar);
//
//    @DeleteMapping("/api/users/delete-photo/{id}/{photoId}")
//    String deletePhoto(@PathVariable Long id , @PathVariable Long photoId);
//
//    @PutMapping("/api/users/update-user-password/{id}")
//    String updateUserPassword(@PathVariable Long id,
//                                                @RequestParam String newPassword,
//                                                @RequestParam String currenPassword);
//    @PostMapping("/api/users/send-email-reset-code")
//    String sendEmailResetCode(@RequestParam String email,
//                                                @RequestParam String newEmail);
//
//    @PostMapping("/api/users/resend-email-resetCode")
//    String resendEmailResetCode(@RequestParam String pendingEmail);
//
//
//}
