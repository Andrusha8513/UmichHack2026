package com.example.support_module.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

    public boolean isOwner(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getId().equals(id);
        }

        return false;
    }

    public boolean isOwnerForEmail(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUsername().equals(email);
        }
        return false;
    }
}