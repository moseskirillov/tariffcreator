package com.fmlogistic.tariffcreator.models.generator.downtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RateModel {
    private Double minRate;
    private Double maxRate;
    private Double rate;
}
