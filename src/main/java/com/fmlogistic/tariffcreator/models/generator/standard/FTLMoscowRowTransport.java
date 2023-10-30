package com.fmlogistic.tariffcreator.models.generator.standard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FTLMoscowRowTransport {
    private String searchName;
    private String zone;
    private String type;
    private String rate;
    private String serviceType;
}
