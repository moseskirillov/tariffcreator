package com.fmlogistic.tariffcreator.models.generator.downtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateDowntimeMsc {
    private String template;
    private String fileName;
    private GenerateDowntimeMscRows rows;
}
