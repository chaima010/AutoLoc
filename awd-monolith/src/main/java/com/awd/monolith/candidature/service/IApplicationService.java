package com.awd.monolith.candidature.service;

import com.awd.monolith.candidature.dto.ApplicationRequest;
import com.awd.monolith.candidature.dto.ApplicationResponse;
import com.awd.monolith.candidature.dto.ApplicationUpdateRequest;

import java.util.List;

/**
 * Interface de service pour la gestion métier des candidatures (applications).
 */
public interface IApplicationService {

    List<ApplicationResponse> search(Long candidateId, Long jobId);

    ApplicationResponse findById(Long id);

    ApplicationResponse create(ApplicationRequest request);

    ApplicationResponse update(Long id, ApplicationUpdateRequest request);

    void delete(Long id);

    default void deleteById(Long id) {
        delete(id);
    }
}

