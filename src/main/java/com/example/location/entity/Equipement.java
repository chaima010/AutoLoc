package com.example.location.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Entity
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipement;

    private String libelle;

    // Côté inverse de la relation ManyToMany
    @ManyToMany(mappedBy = "equipements")
    private List<Vehicule> vehicules;
}