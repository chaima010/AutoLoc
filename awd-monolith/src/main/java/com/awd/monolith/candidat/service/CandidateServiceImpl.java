package com.awd.monolith.candidat.service;

import com.awd.monolith.candidat.dto.CandidateRequest;
import com.awd.monolith.candidat.dto.CandidateResponse;
import com.awd.monolith.candidat.entity.Address;
import com.awd.monolith.candidat.entity.Candidate;
import com.awd.monolith.candidat.mapper.CandidatMapper;
import com.awd.monolith.candidat.repository.ICandidateRepository;
import com.awd.monolith.candidature.repository.IApplicationRepository;
import com.awd.monolith.common.exception.ConflictException;
import com.awd.monolith.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du service métier des candidats.
 *
 * <p>Composant Spring annoté {@link Service}, singleton sans état.
 * Toutes les dépendances sont injectées par constructeur (champs {@code final}
 * et constructeur généré par {@link RequiredArgsConstructor}).
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CandidateServiceImpl implements ICandidateService {

    private final ICandidateRepository candidateRepository;
    private final IAddressService addressService;
    // Monolith coupling: the candidat module reads the candidature table directly.
    // In the microservice version this becomes a REST call to the candidature service.
    private final IApplicationRepository applicationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CandidateResponse> findAll() {
        return candidateRepository.findAll().stream().map(CandidatMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateResponse findById(Long id) {
        return CandidatMapper.toResponse(getCandidate(id));
    }

    @Override
    public CandidateResponse create(CandidateRequest request) {
        if (candidateRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("A candidate with email " + request.email() + " already exists");
        }
        Candidate saved = candidateRepository.save(CandidatMapper.toEntity(request));
        return CandidatMapper.toResponse(saved);
    }

    @Override
    public CandidateResponse update(Long id, CandidateRequest request) {
        Candidate candidate = getCandidate(id);
        if (candidateRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id)) {
            throw new ConflictException("A candidate with email " + request.email() + " already exists");
        }
        candidate.setFirstname(request.firstname());
        candidate.setLastname(request.lastname());
        candidate.setEmail(request.email());

        if (request.address() != null) {
            if (candidate.getAddress() != null) {
                CandidatMapper.updateEntity(candidate.getAddress(), request.address());
            } else {
                candidate.setAddress(CandidatMapper.toEntity(request.address()));
            }
        }
        return CandidatMapper.toResponse(candidateRepository.saveAndFlush(candidate));
    }

    @Override
    public void delete(Long id) {
        Candidate candidate = getCandidate(id);
        if (applicationRepository.existsByCandidateId(id)) {
            throw new ConflictException("Candidate " + id + " has applications; delete them first");
        }
        // cascade also deletes the candidate's address
        candidateRepository.delete(candidate);
    }

    /** Links an existing address to a candidate (replacing any current link). */
    @Override
    public CandidateResponse assignAddress(Long candidateId, Long addressId) {
        Candidate candidate = getCandidate(candidateId);
        Address address = addressService.getAddress(addressId);

        if (address.getCandidate() != null && !address.getCandidate().getId().equals(candidateId)) {
            throw new ConflictException("Address " + addressId + " is already assigned to candidate "
                    + address.getCandidate().getId());
        }
        candidate.setAddress(address);
        return CandidatMapper.toResponse(candidateRepository.saveAndFlush(candidate));
    }

    /** Unlinks the candidate's address. The address itself is kept. */
    @Override
    public CandidateResponse removeAddress(Long candidateId) {
        Candidate candidate = getCandidate(candidateId);
        candidate.setAddress(null);
        return CandidatMapper.toResponse(candidateRepository.saveAndFlush(candidate));
    }

    /** Also used by the candidature module (in-process call inside the monolith). */
    @Override
    public Candidate getCandidate(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate", id));
    }
}

