package com.example.location.entity;

import com.example.location.enums.CategorieVehicule;
import com.example.location.enums.StatutVehicule;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVehicule;

    private String immatriculation;
    private String marque;
    private String modele;

    @Enumerated(EnumType.STRING)
    private CategorieVehicule categorie;

    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    private StatutVehicule statut;

    // Plusieurs véhicules appartiennent à une agence
    @ManyToOne
    @JoinColumn(name = "id_agence")
    private Agence agence;

    // Un véhicule peut avoir plusieurs maintenances
    @OneToMany(mappedBy = "vehicule")
    private List<Maintenance> maintenances;

    // Un véhicule peut être réservé plusieurs fois
    @OneToMany(mappedBy = "vehicule")
    private List<Reservation> reservations;

    // Plusieurs véhicules peuvent utiliser plusieurs équipements
    @ManyToMany
    @JoinTable(
            name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "id_vehicule"),
            inverseJoinColumns = @JoinColumn(name = "id_equipement")
    )
    private List<Equipement> equipements;
}