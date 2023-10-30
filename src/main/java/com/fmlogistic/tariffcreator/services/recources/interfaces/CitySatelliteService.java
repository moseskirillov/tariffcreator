package com.fmlogistic.tariffcreator.services.recources.interfaces;

import com.fmlogistic.tariffcreator.entities.CitySatellite;

import java.util.List;

public interface CitySatelliteService {
    List<CitySatellite> citiesSatellites(String name);
}
