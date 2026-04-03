package com.example.support_module.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

;

@Component
public class JwtService {

    private static final Logger log = LogManager.getLogger(JwtService.class);


    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtAuthenticationDto generateAuthToken(Long userId,String email, Set<Role> roles, Boolean isEnabled, Boolean isAccountNonLocked) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(userId,email, roles, isEnabled, isAccountNonLocked));
        jwtDto.setRefreshToken(generateRefreshJwtToken(email));
        return jwtDto;
    }

    public JwtAuthenticationDto refreshBaseToken(Long userId,String email, Set<Role> roles, Boolean isEnabled, Boolean isAccountNonLocked, String refreshToken) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(userId,email, roles, isEnabled, isAccountNonLocked));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public TokenData extractTokenData(String token) {
        Claims claims = extractAllClaims(token);
        String email = claims.getSubject();
        Long  userId = claims.get("userId" , Long.class);

        List<String> role = claims.get("roles", List.class);
        Set<Role> roles = role != null ?
                role.stream()
                        .map(Role::valueOf)
                        .collect(Collectors.toSet()) :
                Collections.emptySet();

        Boolean isEnabled = claims.get("isEnabled", Boolean.class);
        Boolean isAccountNonLocked = claims.get("accountNonLocked", Boolean.class);

        return new TokenData(userId,email, null, roles, isEnabled, isAccountNonLocked);
    }


    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSingInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException exp) {
            log.error("Ошибка Expired JwtException ", exp);
        } catch (UnsupportedJwtException unx) {
            log.error("Ошибка Unsupported JwtException {} ", String.valueOf(unx));
        } catch (MalformedJwtException mnx) {
            log.error("Ошибка Malformed JwtException {} ", String.valueOf(mnx));
        } catch (SecurityException scp) {
            log.error("Ошибка Security Exception {}", String.valueOf(scp));
        } catch (Exception e) {
            log.error("Ошибка Exception {}", String.valueOf(e));
        }
        return false;
    }

    // можно допилить , добавить проверки и обработку ошибок
    public Set<Role> getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        List<String> rolesList = claims.get("roles", List.class);

        if (rolesList == null) {
            return Collections.emptySet();
        }

        return rolesList.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());

    }

    public Boolean getEnabledFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("isEnabled", Boolean.class);
    }

    private Date getExpiryDate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration();
    }

    public Long getTimeFromToken(String token){
        Date date = getExpiryDate(token);
        return date.getTime() - System.currentTimeMillis();
    }


    private String generateJwtToken(Long userId,
                                    String email,
                                    Set<Role> roles,
                                    Boolean isEnabled,
                                    Boolean isAccountNonLocked) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .claim("userId" , userId)
                .claim("roles", roles.stream()
                        .map(Role::name)
                        .collect(Collectors.toList()))
                .claim("isEnabled", isEnabled)
                .claim("accountNonLocked", isAccountNonLocked)
                .signWith(getSingInKey())
                .compact();
    }

    private String generateRefreshJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSingInKey())
                .compact();
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        LocalDateTime now = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return now;
    }

    public JwtAuthenticationDto refreshRefreshToken(String oldRefreshToken) {

        try {
            if (!validateJwtToken(oldRefreshToken)) {
                throw new SecurityException("Refresh токен умер");
            }

            String email  = getEmailFromToken(oldRefreshToken);

            JwtAuthenticationDto jwtAuthenticationDto = new JwtAuthenticationDto();

            Date expiryDate = getExpiryDate(oldRefreshToken);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expire = convertToLocalDateTime(expiryDate);

            long days = ChronoUnit.DAYS.between(now, expire);

            String newRefreshToken;
            if (days < 7 && days >= 0) {
                newRefreshToken = generateRefreshJwtToken(email);
            }else if(days < 0){
                throw new SecurityException("Refresh токен истек");
            }else {
                newRefreshToken = oldRefreshToken;
            }
            jwtAuthenticationDto.setRefreshToken(newRefreshToken);

            return jwtAuthenticationDto;
        }catch (SecurityException e){
            throw e;
        }catch (Exception e){
            log.error("Ошибка при обновлении refresh токена: {}", e.getMessage(), e);
            throw new SecurityException("Ошибка при обновлении токена");
        }

    }

    private SecretKey getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
