package com.fmlogistic.tariffcreator.models.generator.standard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GenerateLTLRows {
    private String filename;
    private String description;
    private String loadingUnit;
    private String finString;
    private String tariffFormula;
    private List<GenerateLTLRow> rows;
}
