package com.fmlogistic.tariffcreator.models.generator.additional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AdditionalModel {
    private String clientName;
    private String email;
    private String dateFrom;
    private String dateTo;
    private List<AdditionalRow> additional;
}
