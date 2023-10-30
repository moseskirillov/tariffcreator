package com.fmlogistic.tariffcreator.models.generator.prr;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Строка таблицы ПРР")
public class PrrRow {
    @Schema(description = "Название услуги", example = "Погрузо-разгрузочные работы водителем, (ручные)")
    private String serviceName;
    @Schema(description = "Тип услуги", example = "LT")
    private String serviceType;
    @Schema(description = "Стоимость за палет без НДС", example = "100")
    private String cost;
}
