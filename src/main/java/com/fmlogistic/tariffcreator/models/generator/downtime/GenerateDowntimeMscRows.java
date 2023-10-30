package com.fmlogistic.tariffcreator.models.generator.downtime;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GenerateDowntimeMscRows extends BaseModel {

    private String activity;
    private String tariffCode;
    private String description;
    private List<GenerateDowntimeMscRow> firstZoneRows;
    private List<GenerateDowntimeMscRow> secondZoneRows;
    private List<GenerateDowntimeMscRow> thirdZoneRows;
    private List<GenerateDowntimeMscRow> fourthZoneRows;

    public GenerateDowntimeMscRows(
            String clientName,
            String dateFrom,
            String dateTo,
            String activity,
            String tariffCode,
            String description,
            List<GenerateDowntimeMscRow> firstZoneRows,
            List<GenerateDowntimeMscRow> secondZoneRows,
            List<GenerateDowntimeMscRow> thirdZoneRows,
            List<GenerateDowntimeMscRow> fourthZoneRows
    ) {
        super(clientName, dateFrom, dateTo);
        this.activity = activity;
        this.tariffCode = tariffCode;
        this.description = description;
        this.firstZoneRows = firstZoneRows;
        this.secondZoneRows = secondZoneRows;
        this.thirdZoneRows = thirdZoneRows;
        this.fourthZoneRows = fourthZoneRows;
    }

}
