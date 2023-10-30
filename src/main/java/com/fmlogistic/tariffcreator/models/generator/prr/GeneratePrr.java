package com.fmlogistic.tariffcreator.models.generator.prr;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratePrr extends BaseModel {

    public GeneratePrr(String clientName, String dateFrom, String dateTo, String suppliers, String finString, String description, GeneratePrrRow row) {
        super(clientName, dateFrom, dateTo);
        this.suppliers = suppliers;
        this.finString = finString;
        this.description = description;
        this.row = row;
    }

    private String suppliers;
    private String finString;
    private String description;
    private GeneratePrrRow row;
}
