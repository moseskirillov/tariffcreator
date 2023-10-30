package com.fmlogistic.tariffcreator.services.recources;

import com.fmlogistic.tariffcreator.models.resources.CityModel;
import com.fmlogistic.tariffcreator.repositories.CityRepository;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CitiesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CitiesServiceImpl implements CitiesService {

    private final CityRepository cityRepository;

    @Override
    public CityModel findByName(String name) {
        var city = cityRepository.findByName(name);
        if (city.isPresent()) {
            return new CityModel(city.get().getName(), city.get().getZoneCity(), city.get().getZoneRegion(), city.get().isException());
        } else {
            throw new RuntimeException();
        }
    }
}
