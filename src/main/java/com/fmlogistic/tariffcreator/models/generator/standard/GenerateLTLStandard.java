package com.fmlogistic.tariffcreator.models.generator.standard;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenerateLTLStandard extends BaseModel {

    public GenerateLTLStandard(
        String clientName,
        String activity,
        String tariffCode,
        String description,
        String dateFrom,
        String dateTo,
        String suppliers,
        List<GenerateLTLRow> rows) {
        super(clientName, dateFrom, dateTo);
        this.suppliers = suppliers;
        this.activity = activity;
        this.tariffCode = tariffCode;
        this.description = description;
        this.rows = rows;
    }

    private String suppliers;
    private String activity;
    private String tariffCode;
    private String description;
    private List<GenerateLTLRow> rows;

}
