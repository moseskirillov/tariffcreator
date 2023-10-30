package com.fmlogistic.tariffcreator.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cities")
public class City {

    @Id
    @Setter(AccessLevel.NONE)
    @SequenceGenerator(name = "cities_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cities_seq")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "zone_city")
    private String zoneCity;

    @Column(name = "zone_region")
    private String zoneRegion;

    @Column(name = "exception")
    private boolean exception;

}
