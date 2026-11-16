package com.awd.monolith.candidat.service;

import com.awd.monolith.candidat.dto.CandidateRequest;
import com.awd.monolith.candidat.dto.CandidateResponse;
import com.awd.monolith.candidat.entity.Candidate;

import java.util.List;

/**
 * Interface de service pour la gestion métier des candidats.
 *
 * <p>Suit les principes IoC/DI : chaque couche dépend d'abstractions (interfaces)
 * et non d'implémentations concrètes.
 */
public interface ICandidateService {

    List<CandidateResponse> findAll();

    CandidateResponse findById(Long id);

    CandidateResponse create(CandidateRequest request);

    CandidateResponse update(Long id, CandidateRequest request);

    void delete(Long id);

    default void deleteById(Long id) {
        delete(id);
    }

    CandidateResponse assignAddress(Long candidateId, Long addressId);

    CandidateResponse removeAddress(Long candidateId);

    Candidate getCandidate(Long id);
}

