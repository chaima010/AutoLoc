package com.example.location.service;

import com.example.location.entity.Paiement;
import java.util.List;

/**
 * Service en lecture seule pour les paiements.
 * <p>
 * Dans la logique de composition Contrat <-> Paiement, les paiements
 * ne sont créés, modifiés ou supprimés qu'à travers le Contrat.
 */
public interface IPaiementService {

    Paiement findById(Long id);

    List<Paiement> findAll();
}

