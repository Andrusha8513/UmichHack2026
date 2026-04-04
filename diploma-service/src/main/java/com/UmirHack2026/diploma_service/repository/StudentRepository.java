package com.UmirHack2026.diploma_service.repository;

import com.UmirHack2026.diploma_service.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentProfile ,Long> {
}
