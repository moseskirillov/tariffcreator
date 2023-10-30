package com.fmlogistic.tariffcreator.models.generator.downtime;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import com.fmlogistic.tariffcreator.models.generator.additional.AddPointFTLRow;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenerateAddPointFTL extends BaseModel {

    public GenerateAddPointFTL(
            String clientName,
            String dateFrom,
            String dateTo,
            String activity,
            String tariffCode,
            String description,
            List<AddPointFTLRow> rows) {
        super(clientName, dateFrom, dateTo);
        this.activity = activity;
        this.tariffCode = tariffCode;
        this.description = description;
        this.rows = rows;
    }

    private String activity;
    private String tariffCode;
    private String description;
    private List<AddPointFTLRow> rows;
}
