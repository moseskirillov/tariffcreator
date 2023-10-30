package com.fmlogistic.tariffcreator.models.generator.overweight;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Строка таблицы Перевес")
public class OverweightRow {
    private String code;
    @Schema(description = "Норма веса", example = "150")
    private String norm;
    @Schema(description = "Тип услуги", example = "LT")
    private String serviceType;
}
