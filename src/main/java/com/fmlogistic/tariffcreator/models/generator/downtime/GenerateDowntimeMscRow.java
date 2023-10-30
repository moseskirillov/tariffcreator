package com.fmlogistic.tariffcreator.models.generator.downtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GenerateDowntimeMscRow {
    private String zone;
    private String bodyType;
    private String serviceType;
    private List<DowntimeMscRow> downtimeRows;
}
