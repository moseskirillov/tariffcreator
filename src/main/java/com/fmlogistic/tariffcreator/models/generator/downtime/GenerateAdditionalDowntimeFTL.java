package com.fmlogistic.tariffcreator.models.generator.downtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateAdditionalDowntimeFTL {
    private String template;
    private String filename;
    private GenerateDowntimeFTL generateRows;
}
