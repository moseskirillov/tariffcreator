package com.fmlogistic.tariffcreator.models.generator.additional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ForwardingFTL {
    private String template;
    private String fileName;
    private ForwardingFTLRows rows;
}
