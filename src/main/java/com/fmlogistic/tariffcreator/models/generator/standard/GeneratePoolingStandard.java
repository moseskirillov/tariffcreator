package com.fmlogistic.tariffcreator.models.generator.standard;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeneratePoolingStandard extends BaseModel {

    public GeneratePoolingStandard(
            String clientName,
            String dateFrom,
            String dateTo,
            String suppliers,
            String finString,
            String tariffFormula,
            String description,
            List<GeneratePoolingRow> rows) {
        super(clientName, dateFrom, dateTo);
        this.suppliers = suppliers;
        this.finString = finString;
        this.tariffFormula = tariffFormula;
        this.description = description;
        this.rows = rows;
    }

    private String suppliers;
    private String finString;
    private String tariffFormula;
    private String description;
    private List<GeneratePoolingRow> rows;
}
