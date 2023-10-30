package com.fmlogistic.tariffcreator.models.generator.request;

import com.fmlogistic.tariffcreator.models.generator.downtime.DowntimeRow;
import com.fmlogistic.tariffcreator.models.generator.overweight.OverweightRow;
import com.fmlogistic.tariffcreator.models.generator.prr.PrrRow;
import com.fmlogistic.tariffcreator.models.generator.standard.LTLStandard;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Модель LTL запроса на генерацию XML файла тарифа")
public class LTLRequest extends BaseRequest {

    @Schema(description = "Поле поставщика для ПЛМ")
    private String suppliers;
    @Schema(description = "Код товара")
    private String commodityCode;
    @Schema(description = "Признак доставки")
    private boolean ltlDelivery;
    @Schema(description = "Признак возврата")
    private boolean ltlReturn;
    @Schema(description = "Тип возврата")
    private String ltlReturnType;
    @Schema(description = "Признак надбавки")
    private boolean surchargeOversize;
    @Schema(description = "Список строк таблицы LTL доставка")
    private List<LTLStandard> standards;
    @Schema(description = "Список строк таблицы ЖД")
    private List<LTLStandard> railway;
    @Schema(description = "Список строк таблицы Простой")
    private List<DowntimeRow> downtimes;
    @Schema(description = "Список строк таблицы Перевес")
    private List<OverweightRow> overweight;
    @Schema(description = "Список строк таблицы ПРР")
    private List<PrrRow> prr;

    public LTLRequest(
        String suppliers,
        String type,
        String clientName,
        String email,
        String dateFrom,
        String dateTo,
        String comment,
        String commodityCode,
        boolean ltlDelivery,
        boolean ltlReturn,
        String ltlReturnType,
        boolean surchargeOversize,
        List<LTLStandard> standards,
        List<LTLStandard> railway,
        List<DowntimeRow> downtimes,
        List<OverweightRow> overweight,
        List<PrrRow> prr
    ) {
        super(type, clientName, email, dateFrom, dateTo, comment);
        this.suppliers = suppliers;
        this.commodityCode = commodityCode;
        this.ltlDelivery = ltlDelivery;
        this.ltlReturn = ltlReturn;
        this.ltlReturnType = ltlReturnType;
        this.surchargeOversize = surchargeOversize;
        this.standards = standards;
        this.railway = railway;
        this.downtimes = downtimes;
        this.overweight = overweight;
        this.prr = prr;
    }
}