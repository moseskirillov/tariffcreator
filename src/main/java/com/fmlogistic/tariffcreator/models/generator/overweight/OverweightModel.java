package com.fmlogistic.tariffcreator.models.generator.overweight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OverweightModel {
    private String type;
    private String clientName;
    private String email;
    private String dateFrom;
    private String dateTo;
    private String suppliers;
    private List<OverweightRow> overweight;
}
