package com.fmlogistic.tariffcreator.services.recources;

import com.fmlogistic.tariffcreator.entities.CitySatellite;
import com.fmlogistic.tariffcreator.repositories.CitySatelliteRepository;
import com.fmlogistic.tariffcreator.services.recources.interfaces.CitySatelliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CitySatelliteServiceImpl implements CitySatelliteService {

    private final CitySatelliteRepository satelliteRepository;

    @Override
    public List<CitySatellite> citiesSatellites(String name) {
        return satelliteRepository.findByInFormName(name);
    }
}
