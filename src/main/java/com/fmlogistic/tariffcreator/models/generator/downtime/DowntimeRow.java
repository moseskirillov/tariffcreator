package com.fmlogistic.tariffcreator.models.generator.downtime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Строка таблицы Простой")
public class DowntimeRow {
    @Schema(description = "Простои в", example = "Регионы")
    private String region;
    @Schema(description = "Тип услуги", example = "LT")
    private String serviceType;
    @Schema(description = "Время начала платного простоя", example = "12")
    private String time;
    @Schema(description = "Тент", example = "100")
    private String tent;
    @Schema(description = "Изотерм", example = "100")
    private String izoterm;
    @Schema(description = "Реф", example = "100")
    private String ref;
}
