package com.fmlogistic.tariffcreator.models.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseModel {
    private String clientName;
    private String dateFrom;
    private String dateTo;
}
