package com.example.location.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaintenance;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private String description;

    // Chaque maintenance concerne un seul véhicule
    @ManyToOne
    @JoinColumn(name = "id_vehicule")
    private Vehicule vehicule;
}