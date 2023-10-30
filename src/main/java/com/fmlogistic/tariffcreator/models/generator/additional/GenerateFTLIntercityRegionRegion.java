package com.fmlogistic.tariffcreator.models.generator.additional;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenerateFTLIntercityRegionRegion extends BaseModel {
    public GenerateFTLIntercityRegionRegion(
            String clientName,
            String dateFrom,
            String dateTo,
            String activity,
            String deliveryType,
            String tariffCode,
            String description,
            List<GenerateFTLIntercityRegionRegionRow> regRegRows

    ) {
        super(clientName, dateFrom, dateTo);
        this.regRegRows = regRegRows;
        this.activity = activity;
        this.deliveryType = deliveryType;
        this.tariffCode = tariffCode;
        this.description = description;
    }

    private String activity;
    private String deliveryType;
    private String tariffCode;
    private String description;
    private List<GenerateFTLIntercityRegionRegionRow> regRegRows;
}