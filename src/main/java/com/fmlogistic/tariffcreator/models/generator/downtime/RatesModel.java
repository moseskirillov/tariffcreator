package com.fmlogistic.tariffcreator.models.generator.downtime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatesModel {
    private RateModel tent;
    private RateModel izoterm;
    private RateModel ref;
}
