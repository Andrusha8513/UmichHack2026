//package com.example.profile_service;
//
//import com.example.profile_service.dto.FullPrivetProfileDto;;
//import com.example.profile_service.dto.FullPublicProfileDto;
//import com.example.profile_service.feignClient.PostClient;
//import com.example.profile_service.feignClient.UsersProfile;
//import com.example.user_service.dto.PrivetUserProfileDto;
//import com.example.user_service.dto.PublicUserProfileDto;
//import lombok.RequiredArgsConstructor;
//import org.example.postservice.dto.PostDto;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class ProfileService2 {
//    private final UsersProfile usersProfile;
//    private final PostClient postClient;
//
//
//    @Cacheable(value = "private_profile", key = "#id")
//    public FullPrivetProfileDto getMyProfile(Long id) {
//        PrivetUserProfileDto myInfo = usersProfile.getMyProfile(id);
//
//        List<PostDto> myPosts = postClient.findAllPostsByUserId(myInfo.getId());
//
//        return FullPrivetProfileDto.builder()
//                .userDetails(myInfo)
//                .posts(myPosts != null ? myPosts : Collections.emptyList())
//                .build();
//
//    }
//
//    public FullPublicProfileDto getPublicProfile(String email) {
//        PublicUserProfileDto info = usersProfile.findProfile(email);
//
//        List<PostDto> postDtos = postClient.findAllPostsByUserId(info.getId());
//
//        return FullPublicProfileDto.builder()
//                .userDetails(info)
//                .posts(postDtos != null ? postDtos : Collections.emptyList())
//                .build();
//    }
//}
