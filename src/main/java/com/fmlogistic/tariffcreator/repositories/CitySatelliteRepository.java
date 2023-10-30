package com.fmlogistic.tariffcreator.repositories;

import com.fmlogistic.tariffcreator.entities.CitySatellite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitySatelliteRepository extends JpaRepository<CitySatellite, Long> {
    List<CitySatellite> findByInFormName(String name);
}
