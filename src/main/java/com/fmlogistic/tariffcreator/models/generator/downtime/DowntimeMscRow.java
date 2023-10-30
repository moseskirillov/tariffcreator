package com.fmlogistic.tariffcreator.models.generator.downtime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DowntimeMscRow {
    private String hoursFrom;
    private String hoursTo;
    private String delivery;
    private String downtime;
    private String rate;
}
