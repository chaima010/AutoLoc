package com.example.location.entity;

import com.example.location.enums.ModePaiement;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaiement;

    private BigDecimal montant;
    private LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    private ModePaiement modePaiement;

    // Chaque paiement est associé à un contrat unique
    @OneToOne
    @JoinColumn(name = "id_contrat", unique = true)
    @JsonBackReference("contrat-paiement")
    private Contrat contrat;
}