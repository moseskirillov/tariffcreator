package com.fmlogistic.tariffcreator.models.generator.downtime;

import com.fmlogistic.tariffcreator.models.generator.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateDowntime extends BaseModel {

    public GenerateDowntime(
        String clientName,
        String dateFrom,
        String dateTo,
        String suppliers,
        String serviceType,
        RatesModel moscowTwelveRow,
        RatesModel moscowTwentyFourRow,
        RatesModel regionsTwentyFourRow) {
        super(clientName, dateFrom, dateTo);
        this.suppliers = suppliers;
        this.serviceType = serviceType;
        this.moscowTwelveRow = moscowTwelveRow;
        this.moscowTwentyFourRow = moscowTwentyFourRow;
        this.regionsTwentyFourRow = regionsTwentyFourRow;
    }

    private String suppliers;
    private String serviceType;
    private RatesModel moscowTwelveRow;
    private RatesModel moscowTwentyFourRow;
    private RatesModel regionsTwentyFourRow;
}
