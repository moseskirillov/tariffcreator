package com.fmlogistic.tariffcreator.models.user.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на изменение пароля с токеном для проверки пользователя")
public record UpdateRequest(
    @Schema(description = "Токен подтверждения с почты пользователя") String token,
    @Schema(description = "Новый пароль") String password) {
}
