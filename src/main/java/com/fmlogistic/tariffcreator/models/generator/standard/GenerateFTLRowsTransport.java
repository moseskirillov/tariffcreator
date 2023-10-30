package com.fmlogistic.tariffcreator.models.generator.standard;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GenerateFTLRowsTransport extends BaseModel {

    private String activity;
    private String tariffCode;
    private String description;
    private List<FTLMoscowRowTransport> firstZoneRows;
    private List<FTLMoscowRowTransport> secondZoneRows;
    private List<FTLMoscowRowTransport> thirdZoneRows;
    private List<FTLMoscowRowTransport> fourthZoneRows;

    public GenerateFTLRowsTransport(
            String clientName,
            String dateFrom,
            String dateTo,
            String activity,
            String tariffCode,
            String description,
            List<FTLMoscowRowTransport> firstZoneRows,
            List<FTLMoscowRowTransport> secondZoneRows,
            List<FTLMoscowRowTransport> thirdZoneRows,
            List<FTLMoscowRowTransport> fourthZoneRows
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
