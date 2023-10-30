package com.fmlogistic.tariffcreator.models.generator.standard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FTLMoscowRowCapacity {
    private String searchName;
    private String zone;
    private String bodyType;
    private String capacityFrom;
    private String capacityTo;
    private String serviceType;
    private String rate;
}
