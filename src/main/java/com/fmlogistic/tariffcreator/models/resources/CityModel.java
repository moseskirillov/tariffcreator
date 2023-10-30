package com.fmlogistic.tariffcreator.models.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CityModel {
    private String name;
    private String cZone;
    private String rZone;
    private boolean exception;
}
