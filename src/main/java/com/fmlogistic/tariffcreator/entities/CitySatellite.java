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
@Table(name = "cities_satellites")
public class CitySatellite {

    @Id
    @Setter(AccessLevel.NONE)
    @SequenceGenerator(name = "cities_satellites_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cities_satellites_seq")
    private Long id;

    @Column(name = "in_form_name")
    private String inFormName;

    @Column(name = "rZone")
    private String rZone;

    @Column(name = "cZone")
    private String cZone;

    @Column(name = "satellite")
    private String satellite;
}
