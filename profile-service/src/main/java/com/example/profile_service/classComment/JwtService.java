//package com.example.profile_service.security.jwt;
//
//import com.example.profile_service.Role;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import io.jsonwebtoken.io.Decoders;
//
//import javax.crypto.SecretKey;
//import java.util.Collections;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Component
//public class JwtService {
//
//    private static final Logger log = LogManager.getLogger(JwtService.class);
//
//    @Value("58cc2c665709725f73f283b76c4a4d277c038dc8a00d09209672ed631ddda7b2")
//    private String jwtSecret;
//
//    public String getEmailFromToken(String token){
//        Claims  claims  = Jwts.parser()
//                .verifyWith(getSingInKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//        return claims.getSubject();
//    }
//
//    private SecretKey getSingInKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public boolean validateJwtToken(String token) {
//        try {
//            Jwts.parser()
//                    .verifyWith(getSingInKey())
//                    .build()
//                    .parseSignedClaims(token)
//                    .getPayload();
//            return true;
//        } catch (ExpiredJwtException exp) {
//            log.error("Ошибка Expired JwtException ", exp);
//        } catch (UnsupportedJwtException unx) {
//            log.error("Ошибка Unsupported JwtException {} ", String.valueOf(unx));
//        } catch (MalformedJwtException mnx) {
//            log.error("Ошибка Malformed JwtException {} ", String.valueOf(mnx));
//        } catch (SecurityException scp) {
//            log.error("Ошибка Security Exception {}", String.valueOf(scp));
//        } catch (Exception e) {
//            log.error("Ошибка Exception {}", String.valueOf(e));
//        }
//        return false;
//    }
//
//    public Claims extractAllClaims(String token){
//        return Jwts.parser()
//                .verifyWith(getSingInKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    public TokenData extractTokenData(String token){
//        Claims claims = extractAllClaims(token);
//
//        String email = claims.getSubject();
//        Long userId = claims.get("userId" , Long.class);
//
//        List<String> role = claims.get("roles" , List.class);
//        Set<Role> roles = role != null ? role.stream()
//                .map(Role::valueOf)
//                .collect(Collectors.toSet()) :
//                Collections.emptySet();
//
//        Boolean isEnabled = claims.get("isEnabled" , Boolean.class);
//        Boolean isAccountNonLocked = claims.get("accountNonLocked" , Boolean.class);
//
//        return new TokenData(userId , email , null , roles , isEnabled , isAccountNonLocked);
//    }
//}
