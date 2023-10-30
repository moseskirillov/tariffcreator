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
@Schema(description = "Строка таблицы LTL доставка")
public class LTLStandard {
    @Schema(description = "Склад заказчика", example = "TESTCLIENT")
    private String customerWarehouse;
    @Schema(description = "Склад FM", example = "ФМЕКАТ3")
    private String warehouseFm;
    @Schema(description = "Доставка", example = "LT")
    private String deliveryType;
    @Schema(description = "Тип грузовой единицы", example = "EP")
    private String loadingUnit;
    @Schema(description = "Город выгрузки", example = "Абакан")
    private String unloadingCity;
    @Schema(description = "Тип грузополучателя", example = "сети")
    private String consigneeType;
    @Schema(description = "Тип кузова", example = "реф")
    private String bodyType;
    @Schema(description = "Стоимость при количестве паллет 1", example = "100")
    private String one;
    @Schema(description = "Стоимость при количестве паллет 2", example = "100")
    private String two;
    @Schema(description = "Стоимость при количестве паллет 3", example = "100")
    private String three;
    @Schema(description = "Стоимость при количестве паллет 4", example = "100")
    private String four;
    @Schema(description = "Стоимость при количестве паллет 5", example = "100")
    private String five;
    @Schema(description = "Стоимость при количестве паллет 6-8", example = "100")
    private String sixEight;
    @Schema(description = "Стоимость при количестве паллет 9-15", example = "100")
    private String nineFifteen;
    @Schema(description = "Стоимость при количестве паллет 16-20", example = "100")
    private String sixteenTwenty;
    @Schema(description = "Стоимость при количестве паллет 21-25", example = "100")
    private String twentyOneTwentyFive;
}
