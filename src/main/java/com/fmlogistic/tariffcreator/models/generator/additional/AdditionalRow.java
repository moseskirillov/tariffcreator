package com.fmlogistic.tariffcreator.models.generator.additional;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Строка таблицы Pooling Догруз")
public class AdditionalRow {
    @Schema(description = "Место загрузки", example = "4СКИНБАЛАШИСКЛ")
    private String loadingLocation;
    @Schema(description = "Грузополучатель", example = "Абинск")
    private String consignee;
    @Schema(description = "Тип груза", example = "реф")
    private String cargoType;
    @Schema(description = "Стоимость доставки FTL", example = "100")
    private String customerDiscount;
    @Schema(description = "Скидка заказчику FTL", example = "100")
    private String shippingFee;
    @Schema(description = "Тариф за доставку LTL", example = "100")
    private String fenceFee;
    @Schema(description = "Тариф за забор", example = "100")
    private String deliveryCost;
}
