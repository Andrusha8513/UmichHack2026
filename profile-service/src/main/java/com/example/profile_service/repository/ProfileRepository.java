package com.example.profile_service.repository;

import com.example.profile_service.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findById(Long id);

    Optional<Profile> findByEmail(String email);

}
