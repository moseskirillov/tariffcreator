package com.fmlogistic.tariffcreator.models.user.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collection;

@Schema(description = "Ответ на запрос обновления токена")
public record RefreshResponse(
    @Schema(description = "Email пользователя", example = "test@fmlogistic.com")
    String email,
    @Schema(description = "Список ролей пользователя", example = "[\"ROLE_USER\"]")
    Collection<String> roles,
    @Schema(description = "Access токен JWT", example = "skjefhseiohfsifhjs")
    String accessToken,
    @Schema(description = "Refresh token JWT", example = "94eptsuisdorpgu49")
    String refreshToken
) {
}
