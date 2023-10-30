package com.fmlogistic.tariffcreator.models.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на регистрацию нового пользователя")
public record RegisterRequest(
    @Schema(description = "Email пользователя", example = "test@fmlogistic.com")
    @JsonProperty String email,
    @Schema(description = "Пароль пользователя", example = "qwerty123")
    @JsonProperty String password,
    @Schema(description = "Имя пользователя", example = "Иван", nullable = true)
    @JsonProperty String firstName,
    @Schema(description = "Фамилия пользователя", example = "Иванов", nullable = true)
    @JsonProperty String lastName
) {
}
