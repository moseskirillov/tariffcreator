package com.fmlogistic.tariffcreator.models.generator.downtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ForwardingFTLRow {
    private String loadingPlace;
    private String unloadingPlace;
    private String transportMode;
    private String serviceType;
    private String rate;
}
