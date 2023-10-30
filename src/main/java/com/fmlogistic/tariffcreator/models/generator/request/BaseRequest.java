package com.fmlogistic.tariffcreator.models.generator.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    visible = true,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = LTLRequest.class, name = "ltl"),
    @JsonSubTypes.Type(value = FTLRequest.class, name = "ftl"),
    @JsonSubTypes.Type(value = PoolingRequest.class, name = "pooling")
})
@Schema(description = "Базовая модель запроса на генерацию XML файла тарифа")
public abstract class BaseRequest {

    @Schema(description = "Тип запроса", example = "FTL")
    private String type;

    @Schema(description = "Имя клиента", example = "TEST011")
    private String clientName;

    @Schema(description = "Почта пользователя сервиса", example = "mkirillov@fmlogistic.com")
    private String email;

    @Schema(description = "Дата начала действия тарифов", example = "01.01.2023")
    private String dateFrom;

    @Schema(description = "Дата окончания действия тарифов", example = "01.01.2024")
    private String dateTo;

    @Schema(description = "Дополнительная информация для тарифных менеджеров", example = "Обновление тарифов")
    private String comment;

    public BaseRequest(String type, String clientName, String email, String dateFrom, String dateTo, String comment) {
        this.type = type;
        this.clientName = clientName;
        this.email = email;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.comment = comment;
    }
}