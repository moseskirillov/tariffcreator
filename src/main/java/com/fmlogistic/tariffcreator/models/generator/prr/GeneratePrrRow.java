package com.fmlogistic.tariffcreator.models.generator.prr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GeneratePrrRow {
    private String serviceName;
    private String serviceType;
    private String minCost;
    private String maxCost;
}
