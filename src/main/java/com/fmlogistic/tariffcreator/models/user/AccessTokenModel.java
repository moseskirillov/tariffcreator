package com.fmlogistic.tariffcreator.models.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO приведения модели пользователя из БД к модели для генерации Access Token")
public record AccessTokenModel(
    @Schema(description = "Email пользователя", example = "test@fmlogistic.com")
    String email,
    @Schema(description = "Имя пользователя", example = "Иван", nullable = true)
    String firstName,
    @Schema(description = "Фамилия пользователя", example = "Иванов", nullable = true)
    String lastName,
    @Schema(description = "Список ролей пользователя", example = "[\"ROLE_USER\"]")
    List<String> roles
) {}