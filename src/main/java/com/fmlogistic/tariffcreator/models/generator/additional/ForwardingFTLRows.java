package com.fmlogistic.tariffcreator.models.generator.additional;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ForwardingFTLRows extends BaseModel {

    private String activity;
    private String tariffCode;
    private String description;
    private List<ForwardingFTLRow> rows;

    public ForwardingFTLRows(
            String clientName,
            String dateFrom,
            String dateTo,
            String activity,
            String tariffCode,
            String description,
            List<ForwardingFTLRow> rows
    ) {
        super(clientName, dateFrom, dateTo);
        this.activity = activity;
        this.tariffCode = tariffCode;
        this.description = description;
        this.rows = rows;
    }
}
