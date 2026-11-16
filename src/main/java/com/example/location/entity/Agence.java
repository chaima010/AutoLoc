package com.example.location.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    private String nom;
    private String ville;
    private String adresse;
    private String telephone;

    // Une agence possède plusieurs employés
    @OneToMany(mappedBy = "agence")
    @JsonIgnore
    private List<Employe> employes;

    // Une agence gère plusieurs véhicules
    @OneToMany(mappedBy = "agence")
    @JsonIgnore
    private List<Vehicule> vehicules;
}