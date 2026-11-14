package com.example.location.entity;

import com.example.location.enums.RoleEmploye;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmploye;

    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private RoleEmploye role;

    // Plusieurs employés appartiennent à une agence
    @ManyToOne
    @JoinColumn(name = "id_agence")
    private Agence agence;
}