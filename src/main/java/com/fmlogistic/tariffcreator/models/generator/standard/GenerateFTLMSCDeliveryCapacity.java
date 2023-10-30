package com.fmlogistic.tariffcreator.models.generator.standard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateFTLMSCDeliveryCapacity {
    private String template;
    private String fileName;
    private GenerateFTLRowsCapacity rows;
}
