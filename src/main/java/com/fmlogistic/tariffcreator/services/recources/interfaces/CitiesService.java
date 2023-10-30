package com.fmlogistic.tariffcreator.services.recources.interfaces;

import com.fmlogistic.tariffcreator.models.resources.CityModel;

public interface CitiesService {
    CityModel findByName(String name);
}
