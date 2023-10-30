package com.fmlogistic.tariffcreator.models.generator.request;

import com.fmlogistic.tariffcreator.models.generator.additional.AdditionalRow;
import com.fmlogistic.tariffcreator.models.generator.downtime.DowntimeRow;
import com.fmlogistic.tariffcreator.models.generator.overweight.OverweightRow;
import com.fmlogistic.tariffcreator.models.generator.standard.PoolingRow;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PoolingRequest extends BaseRequest {

    @Schema(description = "Поле поставщика для ПЛМ")
    private String suppliers;
    @Schema(description = "Код товара")
    private String commodityCode;
    @Schema(description = "Список строк таблицы Pooling доставка")
    private List<PoolingRow> standard;
    @Schema(description = "Список строк таблицы Простой")
    private List<DowntimeRow> downtimes;
    @Schema(description = "Список строк таблицы Перевес")
    private List<OverweightRow> overweight;
    @Schema(description = "Список строк таблицы Догруз")
    private List<AdditionalRow> additional;

    public PoolingRequest(
        String suppliers,
        String type,
        String clientName,
        String email,
        String dateFrom,
        String dateTo,
        String comment,
        String commodityCode,
        List<PoolingRow> standard,
        List<DowntimeRow> downtimes,
        List<OverweightRow> overweight,
        List<AdditionalRow> additional
    ) {
        super(type, clientName, email, dateFrom, dateTo, comment);
        this.suppliers = suppliers;
        this.commodityCode = commodityCode;
        this.standard = standard;
        this.downtimes = downtimes;
        this.overweight = overweight;
        this.additional = additional;
    }
}