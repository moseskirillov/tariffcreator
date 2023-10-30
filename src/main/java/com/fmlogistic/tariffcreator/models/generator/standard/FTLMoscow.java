package com.fmlogistic.tariffcreator.models.generator.standard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Строка таблицы FTL Москва")
public class FTLMoscow {
    @Schema(description = "Место загрузки", example = "TESTCLIENT")
    private String loadingLocation;
    @Schema(description = "Тип ТС", example = "5т")
    private String truckType;
    @Schema(description = "Вместимость паллет", example = "5-6")
    private String capacity;
    @Schema(description = "Тип кузова", example = "реф")
    private String bodyType;
    @Schema(description = "Мин. оплачиваемое время, час", example = "12")
    private String minimumTime;
    @Schema(description = "1 зона", example = "100")
    private String zoneOne;
    @Schema(description = "2 зона", example = "100")
    private String zoneTwo;
    @Schema(description = "3 зона", example = "100")
    private String zoneThree;
    @Schema(description = "4 зона", example = "100")
    private String zoneFour;
    @Schema(description = "простой", example = "100")
    private String downtime;
    @Schema(description = "доп. точка", example = "100")
    private String additionalPoint;
    @Schema(description = "ПРР", example = "100")
    private String prr;
    @Schema(description = "Экспедирование", example = "100")
    private String forwarding;
}
