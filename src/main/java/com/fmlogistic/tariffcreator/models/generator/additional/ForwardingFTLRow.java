package com.fmlogistic.tariffcreator.models.generator.additional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ForwardingFTLRow {
    private String zone;
    private String tonnage;
    private String serviceType;
    private String rate;
}
