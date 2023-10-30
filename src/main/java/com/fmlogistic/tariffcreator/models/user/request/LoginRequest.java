package com.fmlogistic.tariffcreator.models.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на логин")
public record LoginRequest(
    @Schema(description = "Email пользователя", example = "test@fmlogistic.com")
    @JsonProperty String email,
    @Schema(description = "Пароль пользователя", example = "qwerty123")
    @JsonProperty String password) {
}
