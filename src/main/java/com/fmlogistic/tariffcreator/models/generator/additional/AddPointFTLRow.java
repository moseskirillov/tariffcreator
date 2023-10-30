package com.fmlogistic.tariffcreator.models.generator.additional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddPointFTLRow {
    private String loadingPlace;
    private String unloadingPlace;
    private String transportMode;
    private String serviceType;
    private String minRate;
    private String rate;
}
