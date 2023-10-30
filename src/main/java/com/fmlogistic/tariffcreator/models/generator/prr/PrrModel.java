package com.fmlogistic.tariffcreator.models.generator.prr;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PrrModel {
    private String type;
    private String clientName;
    private String email;
    private String dateFrom;
    private String dateTo;
    private String suppliers;
    private List<PrrRow> rows;
}
