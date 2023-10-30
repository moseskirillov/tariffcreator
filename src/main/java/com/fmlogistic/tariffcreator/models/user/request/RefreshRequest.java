package com.fmlogistic.tariffcreator.models.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на обновление JWT токена")
public record RefreshRequest(@JsonProperty String refreshToken) {}
