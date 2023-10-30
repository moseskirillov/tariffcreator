package com.fmlogistic.tariffcreator.models.generator.additional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Строка таблицы Межгород достава")
public class FTLIntercityModel {
    @Schema(description = "Пункт отправления", example = "Абинск")
    private String destination;
    @Schema(description = "Пункт назначения", example = "Абинск")
    private String transportType;
    @Schema(description = "Тип ТС", example = "3т")
    private String tent;
    @Schema(description = "Тент, руб. без НДС", example = "100")
    private String izo;
    @Schema(description = "Изо, руб. без НДС", example = "100")
    private String ref;
    @Schema(description = "Реф, руб. без НДС", example = "100")
    private String departure;
}
