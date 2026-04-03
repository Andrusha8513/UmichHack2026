//package com.example.profile_service.feignClient;
//
//import org.example.postservice.dto.PostDto;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.List;
//@FeignClient(name = "posts")
//public interface PostClient {
//    @GetMapping("/api/post/api/post/{userId}")
//    List<PostDto> findAllPostsByUserId(@PathVariable Long userId);
//}
