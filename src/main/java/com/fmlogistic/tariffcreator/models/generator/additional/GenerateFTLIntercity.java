package com.fmlogistic.tariffcreator.models.generator.additional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GenerateFTLIntercity {
    private List<GenerateFTLIntercityMscRegion> mscRegionRows;
    private List<GenerateFTLIntercityRegionMsc> regionMscRows;
    private List<GenerateFTLIntercityRegionRegion> regionRegionRows;
}
