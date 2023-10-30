package com.fmlogistic.tariffcreator.models.generator.downtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateForwardingFTL {
    private String template;
    private String filename;
    private ForwardingFTL generateRows;
}
