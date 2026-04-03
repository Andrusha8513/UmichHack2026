package com.example.support_module.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NewJwtService {

    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(30);
    private static final long REFRESH_RENEW_THRESHOLD_DAYS = 7;

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_IS_ENABLED = "isEnabled";
    private static final String CLAIM_IS_ACCOUNT_NON_LOCKED = "isAccountNonLocked";

    @Value("${jwt.secret}")
    private String jwtSecret;

    // выдает пару токенов при успешном логине
    public JwtAuthenticationDto generateTokenPair(TokenData tokenData) {
        JwtAuthenticationDto jwtAuthenticationDto = new JwtAuthenticationDto();
        jwtAuthenticationDto.setToken(buildAccessToken(tokenData));
        jwtAuthenticationDto.setRefreshToken(buildRefreshToken(tokenData.getEmail()));
        return jwtAuthenticationDto;
    }

    //обновляет access токен и при необходимости перевыпускает рефреш токен
    public JwtAuthenticationDto refreshTokens(TokenData tokenData ,
                                              RefreshTokenDto refreshTokenDto) {

        String refreshToken = refreshTokenDto.getRefreshToken();
        Claims claims = parseClaimsOrThrowRefresh(refreshToken);

        long daysLeft = Duration.between(Instant.now(),
                claims.getExpiration().toInstant()).toDays();
        String newRefreshToken = (daysLeft < REFRESH_RENEW_THRESHOLD_DAYS)
                ? buildRefreshToken(claims.getSubject()) : refreshToken;
        JwtAuthenticationDto jwtAuthenticationDto = new JwtAuthenticationDto();
        jwtAuthenticationDto.setToken(buildAccessToken(tokenData));
        jwtAuthenticationDto.setRefreshToken(newRefreshToken);
        return jwtAuthenticationDto;
    }
    //проверяет подписи и срок жизни токена
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT истёк: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e)  {
            log.warn("JWT не поддерживается: {}", e.getMessage());
        }
        catch (MalformedJwtException e)    {
            log.warn("JWT повреждён: {}", e.getMessage());
        }
        catch (SignatureException e)       {
            log.warn("JWT неверная подпись: {}", e.getMessage());
        }
        catch (IllegalArgumentException e) {
            log.warn("JWT null: {}", e.getMessage());
        }
        return false;
    }
    // извлекает все данные пользователя из access токена
    public TokenData extractTokenData(String token) {
        Claims claims = parseClaims(token);
        return new TokenData(
                claims.get(CLAIM_USER_ID , Long.class),
                claims.getSubject(),
                null,
                extractRoles(claims),
                claims.get(CLAIM_IS_ENABLED , Boolean.class),
                claims.get(CLAIM_IS_ACCOUNT_NON_LOCKED , Boolean.class)
        );
    }
    // извлекает емеил из любого токена
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }
    //проверяет, что рефреш токен не истек и принадлежит пользователю по емеил
    public void validateRefreshToken(RefreshTokenDto refreshTokenDto , String expectedEmail) {
        Claims claims = parseClaimsOrThrowRefresh(refreshTokenDto.getRefreshToken());
        String tokenEmail = claims.getSubject();
        if (!expectedEmail.equals(tokenEmail)) {
            throw new JwtRefreshException("Refresh токен не принадлежит пользователю: " + expectedEmail);
        }
    }
    //возвращает дату истечения токена
    public Date extractExpiration(String token) {
        return parseClaims(token).getExpiration();
    }
    // возвращает миллисекунды до истечения токена
    public long millisUntilExpiry(String token) {
        return parseClaims(token).getExpiration().toInstant().toEpochMilli()
                - Instant.now().toEpochMilli();
    }
    // проверяет истек ли токен
    public boolean isTokenExpired(String token) {
        try{
            return parseClaims(token).getExpiration().before(new Date());
        }catch (ExpiredJwtException e){
            return true;
        }
    }
    // Построение токена
    private String buildAccessToken(TokenData tokenData) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(tokenData.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ACCESS_TOKEN_DURATION)))
                .claim(CLAIM_USER_ID , tokenData.getId())
                .claim(CLAIM_ROLES , tokenData.getRoles()
                        .stream().map(Role::name).collect(Collectors.toList()))
                .claim(CLAIM_IS_ENABLED , tokenData.getIsEnabled())
                .claim(CLAIM_IS_ACCOUNT_NON_LOCKED , tokenData.getIsAccountNonLocked())
                .signWith(signingKey())
                .compact();
    }
    private String buildRefreshToken(String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(REFRESH_TOKEN_DURATION)))
                .signWith(signingKey())
                .compact();
    }

    private Claims parseClaims(String  token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Claims parseClaimsOrThrowRefresh(String token) {
        try {
            return parseClaims(token);
        } catch (ExpiredJwtException e) {
            throw new JwtRefreshException("Refresh-токен истёк", e);
        } catch (JwtException e) {
            throw new JwtRefreshException("Refresh-токен невалиден", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Set<Role> extractRoles(Claims claims) {
        List<String> rolesNames = claims.get(CLAIM_ROLES, List.class);
        if (rolesNames == null || rolesNames.isEmpty()) {
            return Collections.emptySet();
        }
        return rolesNames.stream()
                .map(Role::valueOf)
                .collect(Collectors.toUnmodifiableSet());
    }

    private SecretKey signingKey(){
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
