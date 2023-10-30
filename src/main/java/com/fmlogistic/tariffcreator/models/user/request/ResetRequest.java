package com.fmlogistic.tariffcreator.models.user.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на сброс пароля пользователя")
public record ResetRequest(
    @Schema(
        description = "Email пользователя для которого сбрасывается пароль",
        example = "test@test.test")
    String email) {
}
