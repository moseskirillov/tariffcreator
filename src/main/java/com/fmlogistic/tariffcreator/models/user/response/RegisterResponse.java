package com.fmlogistic.tariffcreator.models.user.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Deprecated
@Schema(
    description = "Ответ на регистрацию, на данный момент проверка идет по статус кодам, так что не актуально",
    example = "SUCCESS"
)
public enum RegisterResponse {
    SUCCESS,
    ERROR,
    DUPLICATE
}