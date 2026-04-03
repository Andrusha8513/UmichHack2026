package com.user_service.user_service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users , Long> {
    Optional<Users> findByEmail(String email);

    Optional<Users> findByConfirmationCode(String code);

    Optional<Users> findByPendingEmail(String pendingEmail);
    Optional<Users> getUserByEmail(String email);
    Optional<Users> getUserBySecondName(String secondName);
}
