package com.fmlogistic.tariffcreator.models.generator.additional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Строка таблицы Доп.услуги межгород")
@AllArgsConstructor
public class FTLAdditionalModel {
    @Schema(description = "Место загрузки", example = "Moscow")
    private String loadingLocation;
    @Schema(description = "Место выгрузки", example = "Moscow")
    private String unloadingLocation;
    @Schema(description = "Тип ТС", example = "20т")
    private String vehicleType;
    @Schema(description = "Название услуги", example = "Дополнительная точка выгрузки в городе")
    private String serviceName;
    @Schema(description = "Тент", example = "100")
    private String tent;
    @Schema(description = "Изо", example = "100")
    private String izoterm;
    @Schema(description = "Реф", example = "100")
    private String ref;
}
