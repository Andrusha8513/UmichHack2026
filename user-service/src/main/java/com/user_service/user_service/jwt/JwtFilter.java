package com.user_service.user_service.jwt;


import com.example.support_module.jwt.JwtService;
import com.example.support_module.jwt.NewJwtService;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final NewJwtService newJwtService;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token != null && newJwtService.validateToken(token)) {
            log.info("Token получен: {}", token);
            jwtService.refreshRefreshToken(token);

          TokenData tokenData =  newJwtService.extractTokenData(token);

            List<GrantedAuthority> authorities = tokenData.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.name()))
                    .collect(Collectors.toList());

            UserDetails userDetails = new CustomUserDetails(
                    tokenData.getId(),
                    tokenData.getEmail(),
                    "",
                    tokenData.getIsEnabled(),
                    !tokenData.getIsAccountNonLocked(),
                    authorities
            );



            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }



    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }



//    private void setCustomDetailsToSecurityContextHolder(String token) {
//        String email = jwtService.getEmailFromToken(token);
//        Set<Role> roles = jwtService.getRoleFromToken(token);
//        CustomUserDetails customUserDerails = customUserService.loadUserByUsername(email);
//        List<GrantedAuthority> authorities = roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role.name()))
//                .collect(Collectors.toList());
//
//        //пока для тестов отключил
////        if (!customUserDerails.isEnabled()){
////            throw new DisabledException("Аккаунт  не активирован");
////        }
//
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customUserDerails, null,
//                authorities);
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//    }
}
