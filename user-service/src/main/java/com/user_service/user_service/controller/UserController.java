package com.user_service.user_service.controller;


import com.example.support_module.jwt.Role;

import com.user_service.user_service.UserService;
import com.user_service.user_service.dto.UserRegistrationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    // Регистрация нового пользователя
    @PostMapping("/registration")
    public ResponseEntity<String> registrationUser(@RequestBody UserRegistrationDTO usersDto) {
        try {
            userService.createUsers(usersDto);
            return ResponseEntity.ok().body("Пользователь зарегистрирован");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Подтверждение регистрации по коду
    @PostMapping("/confirm-registration")
    public ResponseEntity<String> confirmRegistration(@RequestParam("code") String code) {

        boolean isEnabled = userService.confirmRegistration(code);

        if (isEnabled) {
            return ResponseEntity.ok("Аккаунт успешно подтверждён");
        }
        return ResponseEntity.badRequest().body("Неверный код регистрации!");
    }

    // Повторная отправка кода подтверждения
    @PostMapping("/resend-confirm-registration")
    public ResponseEntity<String> resendConfirmationCode(@RequestParam String email) {
        try {
            userService.resendConfirmationCode(email);
            return ResponseEntity.ok("Повторно отправлен код для регистрации");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // Отправка кода для сброса пароля
    @PostMapping("/send-password-resetCode")
    public ResponseEntity<String> sendPasswordResetCode(@RequestParam String email) {
        try {
            userService.sendPasswordResetCode(email);
            return ResponseEntity.ok("Код для сброса пароля отправлен на почту.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Сброс пароля с использованием кода
    @PostMapping("/reset-password-with-code")
    public ResponseEntity<String> resetPasswordWithCode(@RequestParam String email,
                                                        @RequestParam String code,
                                                        @RequestParam String newPassword) {
        try {
            userService.resetPasswordWithCode(email, code, newPassword);
            return ResponseEntity.ok("Пароль успешно изменён");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Отправка кода для смены email
    @PostMapping("/send-email-reset-code")
    @PreAuthorize("@securityService.isOwnerForEmail(#email) or hasAuthority('ROLE_ADMIN')")
    // не тестил как работает защита , но по идее должна работать
    public ResponseEntity<String> sendEmailResetCode(@RequestParam String email,
                                                     @RequestParam String newEmail) {
        try {
            userService.sendEmailResetCode(email, newEmail);
            return ResponseEntity.ok("Код для смены почты отправлен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Повторная отправка кода для смены email
    @PostMapping("/resend-email-resetCode")
    public ResponseEntity<String> resendEmailResetCode(@RequestParam String pendingEmail) {
        try {
            userService.resendEmailResetCode(pendingEmail);
            return ResponseEntity.ok("Код был повторно отправлен");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Обновление пароля пользователя
    @PutMapping("/update-user-password/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateUserPassword(@PathVariable Long id,
                                                     @RequestParam String newPassword,
                                                     @RequestParam String currenPassword) {
        try {
            userService.updateUserPassword(id, newPassword, currenPassword);
            return ResponseEntity.ok("Пароль успешно изменён");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Подтверждение смены email
    @PostMapping("/confirm-email-change/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> confirmEmailChange(@PathVariable Long id,
                                                     @RequestParam String code) {
        try {
            userService.confirmEmailChange(id, code);
            return ResponseEntity.ok("Почта успешно изменена");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Обновление ролей пользователя
    @PutMapping("/update-roles/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')") //  ПОКА ДЛЯ ТЕСТОВ OR
    public ResponseEntity<String> updateRoles(@PathVariable Long id,
                                              @RequestBody Set<Role> newRole) {
        try {
            userService.updateRoles(id, newRole);
            return ResponseEntity.ok("Роли успешно изменены");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Получение списка всех пользователей (только для администратора)
    @GetMapping("/get-all-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserRegistrationDTO>> getAllUsers() {
        List<UserRegistrationDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Изменение статуса аккаунта (enable/disable)
    @PutMapping("/changeAccountStatus/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> changeAccountStatus(@PathVariable Long id,
                                                      @RequestParam boolean newAccountStatus) {
        try {

            userService.changeAccountStatus(id, newAccountStatus);
            return ResponseEntity.ok("Всё прошло  успешно , статус аккаунт теперь " + newAccountStatus);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // Блокировка или разблокировка аккаунта
    @PutMapping("/accountBlocking/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> accountBlocking(@PathVariable Long id,
                                                  @RequestParam boolean newAccountStatus) {
        try {




            userService.accountBlocking(id, newAccountStatus);
            return ResponseEntity.ok("Всё прошло  успешно , статус аккаунт теперь " + newAccountStatus);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Обновление имени пользователя
    @PutMapping("/updateName/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateName(@PathVariable Long id,
                                             @RequestParam String newName) {
        try {
            userService.updateName(id, newName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Обновление фамилии пользователя
    @PutMapping("/updateSecondName/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateSecondName(@PathVariable Long id,
                                                   @RequestParam String newSecondName) {
        try {
            userService.updateSecondName(id, newSecondName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Обновление отчества  пользователя
    @PutMapping("/updateSurName/{id}")
    @PreAuthorize("@securityService.isOwner(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateSurName(@PathVariable Long id,
                                                   @RequestParam String newSurName) {
        try {
            userService.updateSurName(id, newSurName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}