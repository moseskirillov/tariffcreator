package com.fmlogistic.tariffcreator.models.generator.additional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GenerateAdditionalRow {
    private String searchName;
    private String zone;
    private String unloadingCity;
    private String bodyType;
    private String serviceType;
    private String deliveryCost;
    private String customerDiscount;
    private String shippingFee;
    private String fenceFee;
}
