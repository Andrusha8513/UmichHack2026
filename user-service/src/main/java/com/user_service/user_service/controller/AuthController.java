package com.user_service.user_service.controller;

import com.example.support_module.jwt.JwtAuthenticationDto;
import com.example.support_module.jwt.RefreshTokenDto;
import com.user_service.user_service.UserService;
import com.user_service.user_service.dto.UserCredentialsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

//@CrossOrigin(origins = "*") вместо клобального cors  , но надо протеститровать
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDto> singIn(@RequestBody UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        try {
    JwtAuthenticationDto jwtAuthenticationDto = userService.singIn(userCredentialsDto);
    return ResponseEntity.ok(jwtAuthenticationDto);
        }catch (AuthenticationException e){
            throw new RuntimeException("Аутентификация не удалась" + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader){
        try {
            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("Неверный заголовок авторизации братик");
            }


            String accessToken = authHeader.substring(7);

            userService.logout(accessToken);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/fullLogout/{id}")
    public ResponseEntity<String> fullLogout(@PathVariable Long id ,
                                            @RequestHeader("Authorization") String authHeder){
        try {
            if(authHeder == null || !authHeder.startsWith("Bearer ")){
                return ResponseEntity.badRequest().body("Неверный заголовок авторизации братик");
            }

            String accessToken = authHeder.substring(7);

            userService.fullLogout(id, accessToken);
            return ResponseEntity.ok("Успешно вышли со всех аккаунтов");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
