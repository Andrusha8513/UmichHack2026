package com.example.profile_service.security.jwt;

import com.example.support_module.jwt.JwtService;
import com.example.support_module.jwt.TokenData;
import com.example.support_module.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token != null && jwtService.validateJwtToken(token)) {
            log.info("Token получен: {}", token);
            TokenData tokenData = jwtService.extractTokenData(token);
            List<GrantedAuthority> authorities = tokenData.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.name()))
                    .collect(Collectors.toList());


            UserDetails userDetails = new CustomUserDetails(
                    tokenData.getId(),
                    tokenData.getEmail(),
                    "",
                    authorities
            );

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Для пользователя установлена аутентификация: {}", tokenData.getEmail());
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearToken != null && bearToken.startsWith("Bearer ")) {
            return bearToken.substring(7);
        }
        return null;
    }
}
