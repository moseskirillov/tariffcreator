package com.fmlogistic.tariffcreator.repositories;

import com.fmlogistic.tariffcreator.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findByName(String name);
}
