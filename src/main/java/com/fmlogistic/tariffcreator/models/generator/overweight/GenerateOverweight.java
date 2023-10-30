package com.fmlogistic.tariffcreator.models.generator.overweight;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateOverweight extends BaseModel {

    public GenerateOverweight(
        String clientName,
        String dateFrom,
        String dateTo,
        String serviceType,
        String suppliers,
        OverweightRow row) {
        super(clientName, dateFrom, dateTo);
        this.suppliers = suppliers;
        this.serviceType = serviceType;
        this.row = row;
    }

    private String suppliers;
    private String serviceType;
    private OverweightRow row;
}
