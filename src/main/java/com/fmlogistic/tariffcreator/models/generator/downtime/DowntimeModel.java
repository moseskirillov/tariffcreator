package com.fmlogistic.tariffcreator.models.generator.downtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DowntimeModel {
    private String type;
    private String clientName;
    private String email;
    private String dateFrom;
    private String dateTo;
    private String suppliers;
    private List<DowntimeRow> rows;
}
