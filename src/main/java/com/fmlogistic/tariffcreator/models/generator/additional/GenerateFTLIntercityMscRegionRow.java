package com.fmlogistic.tariffcreator.models.generator.additional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateFTLIntercityMscRegionRow {
    private String loadingZone;
    private String digitalZone;
    private String unloadingZone;
    private String unloadingCity;
    private String transportType;
    private String serviceType;
    private String min;
    private String max;
    private String rate;
}
