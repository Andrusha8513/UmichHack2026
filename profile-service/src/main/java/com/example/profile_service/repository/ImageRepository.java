package com.example.profile_service.repository;

import com.example.profile_service.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image , Long> {
}
