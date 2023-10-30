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
@Schema(description = "Строка таблицы Pooling доставка")
public class PoolingRow {
    @Schema(description = "Склад заказчика", example = "TESTCLIENT")
    private String customerWarehouse;
    @Schema(description = "Склад FM", example = "ФМЕКАТ3")
    private String warehouseFm;
    @Schema(description = "Доставка", example = "PL")
    private String delivery;
    @Schema(description = "Грузовая единица", example = "EP")
    private String loadingUnit;
    @Schema(description = "Город выгрузки", example = "Абинск")
    private String unloadingCity;
    @Schema(description = "Тип кузова", example = "реф")
    private String bodyType;
    @Schema(description = "Стоимость доставки при количестве паллет 1-15", example = "100")
    private String oneFifteen;
    @Schema(description = "Стоимость доставки при количестве паллет 16-20", example = "100")
    private String sixteenTwenty;
    @Schema(description = "Стоимость доставки при количестве паллет 21-25", example = "100")
    private String twentyOneTwentyFive;
    @Schema(description = "Стоимость доставки при количестве паллет 26", example = "100")
    private String twentySix;
    @Schema(description = "Стоимость доставки при количестве паллет 27", example = "100")
    private String twentySeven;
    @Schema(description = "Стоимость доставки при количестве паллет 28", example = "100")
    private String twentyEight;
    @Schema(description = "Стоимость доставки при количестве паллет 29", example = "100")
    private String twentyNine;
    @Schema(description = "Стоимость доставки при количестве паллет 30", example = "100")
    private String thirty;
    @Schema(description = "Стоимость доставки при количестве паллет 31", example = "100")
    private String thirtyOne;
    @Schema(description = "Стоимость доставки при количестве паллет 32", example = "100")
    private String thirtyTwo;
    @Schema(description = "Стоимость доставки при количестве паллет 33", example = "100")
    private String thirtyThree;
}