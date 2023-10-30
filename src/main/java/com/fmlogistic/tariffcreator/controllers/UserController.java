package com.fmlogistic.tariffcreator.controllers;

import com.fmlogistic.tariffcreator.models.user.request.LoginRequest;
import com.fmlogistic.tariffcreator.models.user.request.RefreshRequest;
import com.fmlogistic.tariffcreator.models.user.request.RegisterRequest;
import com.fmlogistic.tariffcreator.models.user.request.ResetRequest;
import com.fmlogistic.tariffcreator.models.user.request.UpdateRequest;
import com.fmlogistic.tariffcreator.models.user.response.LoginResponse;
import com.fmlogistic.tariffcreator.models.user.response.RefreshResponse;
import com.fmlogistic.tariffcreator.models.user.response.RegisterResponse;
import com.fmlogistic.tariffcreator.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(
    name = "Для регистрации, авторизации",
    description = "Регистрация, авторизация, изменение и сброс пароля, получение токенов"
)
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Вход пользователя в UI")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("Получен запрос на вход в систему: {}", request);
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        log.info("Получен запрос на регистрацию: {}", request);
        return ResponseEntity.ok(userService.registration(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Обновление JWT токена")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest request) {
        log.info("Получен запрос на обновление токена: {}", request);
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    @PostMapping("/update")
    @Operation(summary = "Обновление пароля пользователя")
    public ResponseEntity<String> update(@RequestBody UpdateRequest request) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    @PostMapping("/reset")
    @Operation(summary = "Сброс пароля пользователя")
    public ResponseEntity<Void> reset(@RequestBody ResetRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/reset/changePassword")
    @Operation(summary = "Изменение пароля через подтверждение email")
    public RedirectView change(@RequestParam String token) {
        userService.changePassword(token);
        return new RedirectView("https://k8s-ru.fmlogistic.com/lsc-team/tariff-creator/changePassword?token=" + token);
    }
}