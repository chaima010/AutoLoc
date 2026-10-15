package com.example.location.entity;

import com.example.location.enums.StatutReservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    // Plusieurs réservations peuvent appartenir au même client
    @ManyToOne
    @JoinColumn(name = "id_client")
    private Client client;

    // Plusieurs réservations peuvent concerner le même véhicule
    @ManyToOne
    @JoinColumn(name = "id_vehicule")
    private Vehicule vehicule;

    // Une réservation est associée à un contrat
    @OneToOne(mappedBy = "reservation")
    private Contrat contrat;
}