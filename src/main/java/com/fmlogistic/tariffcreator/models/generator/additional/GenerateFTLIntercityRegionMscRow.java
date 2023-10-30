package com.fmlogistic.tariffcreator.models.generator.additional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateFTLIntercityRegionMscRow {
    private String loadingZone;
    private String loadingCity;
    private String unloadingZone;
    private String digitalZone;
    private String transportType;
    private String serviceType;
    private String min;
    private String max;
    private String rate;
    private int queue;
    private int finalQueue;
}
