package com.fmlogistic.tariffcreator.models.generator.standard;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GenerateFTLRowsCapacity extends BaseModel {

    private String activity;
    private String tariffCode;
    private String description;
    private List<FTLMoscowRowCapacity> firstZoneRows;
    private List<FTLMoscowRowCapacity> secondZoneRows;
    private List<FTLMoscowRowCapacity> thirdZoneRows;
    private List<FTLMoscowRowCapacity> fourthZoneRows;

    public GenerateFTLRowsCapacity(
            String clientName,
            String dateFrom,
            String dateTo,
            String activity,
            String tariffCode,
            String description,
            List<FTLMoscowRowCapacity> firstZoneRows,
            List<FTLMoscowRowCapacity> secondZoneRows,
            List<FTLMoscowRowCapacity> thirdZoneRows,
            List<FTLMoscowRowCapacity> fourthZoneRows
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
