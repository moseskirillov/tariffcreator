package com.fmlogistic.tariffcreator.models.generator.request;

import com.fmlogistic.tariffcreator.models.generator.additional.FTLAdditionalModel;
import com.fmlogistic.tariffcreator.models.generator.additional.FTLIntercityModel;
import com.fmlogistic.tariffcreator.models.generator.standard.FTLMoscow;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Модель FTL запроса на генерацию XML файла тарифа")
public class FTLRequest extends BaseRequest {

    @Schema(description = "Вид тарификации", example = "По типу ТС")
    private String billing;
    @Schema(description = "Список строк FTL Москва")
    private List<FTLMoscow> moscow;
    @Schema(description = "Список строк Межгород")
    private List<FTLIntercityModel> intercity;
    @Schema(description = "Список строк Доп услуги")
    private List<FTLAdditionalModel> additional;

    public FTLRequest(
        String type,
        String clientName,
        String email,
        String dateFrom,
        String dateTo,
        String comment,
        String billing,
        List<FTLMoscow> moscow,
        List<FTLIntercityModel> intercity,
        List<FTLAdditionalModel> additional)
    {
        super(type, clientName, email, dateFrom, dateTo, comment);
        this.billing = billing;
        this.moscow = moscow;
        this.intercity = intercity;
        this.additional = additional;
    }
}
