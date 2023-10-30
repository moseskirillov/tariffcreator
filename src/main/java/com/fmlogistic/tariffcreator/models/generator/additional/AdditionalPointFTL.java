package com.fmlogistic.tariffcreator.models.generator.additional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdditionalPointFTL {
    private String template;
    private String fileName;
    private AdditionalPointFTLRows rows;
}
