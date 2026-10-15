package com.example.location.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContrat;

    private LocalDate dateSignature;
    private BigDecimal montantTotal;
    private boolean valide;

    // Chaque contrat concerne une réservation unique
    @OneToOne
    @JoinColumn(name = "id_reservation", unique = true)
    private Reservation reservation;

    // Côté inverse de la relation avec Paiement
    @OneToOne(mappedBy = "contrat")
    private Paiement paiement;
}