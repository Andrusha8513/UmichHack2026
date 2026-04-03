package com.example.support_module.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class TokenData {
    private Long id;
    private String email;
    private String password;
    private Set<Role> roles;
    private Boolean isEnabled;
    private Boolean isAccountNonLocked;
}
