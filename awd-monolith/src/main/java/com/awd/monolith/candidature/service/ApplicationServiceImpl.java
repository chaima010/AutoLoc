package com.awd.monolith.candidature.service;

import com.awd.monolith.candidat.entity.Candidate;
import com.awd.monolith.candidat.service.ICandidateService;
import com.awd.monolith.candidature.dto.ApplicationRequest;
import com.awd.monolith.candidature.dto.ApplicationResponse;
import com.awd.monolith.candidature.dto.ApplicationUpdateRequest;
import com.awd.monolith.candidature.entity.Application;
import com.awd.monolith.candidature.mapper.ApplicationMapper;
import com.awd.monolith.candidature.repository.IApplicationRepository;
import com.awd.monolith.common.exception.ConflictException;
import com.awd.monolith.common.exception.ResourceNotFoundException;
import com.awd.monolith.job.entity.Job;
import com.awd.monolith.job.service.IJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implémentation du service métier des candidatures.
 *
 * <p>Dans le monolithe, le candidat et le job sont injectés et manipulés via leurs
 * interfaces de service respectives ({@link ICandidateService} et {@link IJobService}).
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ApplicationServiceImpl implements IApplicationService {

    private final IApplicationRepository applicationRepository;
    private final ICandidateService candidateService;
    private final IJobService jobService;

    /** Lists candidatures, optionally filtered by candidate and/or job. */
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> search(Long candidateId, Long jobId) {
        if (candidateId != null) {
            candidateService.getCandidate(candidateId); // 404 if unknown
        }
        if (jobId != null) {
            jobService.getJob(jobId); // 404 if unknown
        }
        return applicationRepository.search(candidateId, jobId).stream()
                .map(ApplicationMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse findById(Long id) {
        return ApplicationMapper.toResponse(getApplication(id));
    }

    /**
     * Business rules:
     * - the candidate and the job must exist (404)
     * - the job must be available (409)
     * - a candidate cannot apply twice to the same job (409)
     */
    @Override
    public ApplicationResponse create(ApplicationRequest request) {
        Candidate candidate = candidateService.getCandidate(request.candidateId());
        Job job = jobService.getJob(request.jobId());

        if (!job.isAvailable()) {
            throw new ConflictException("Job " + job.getId() + " is not available anymore");
        }
        if (applicationRepository.existsByCandidateIdAndJobId(candidate.getId(), job.getId())) {
            throw new ConflictException("Candidate " + candidate.getId() + " already applied to job " + job.getId());
        }

        Application application = new Application();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setMotivation(request.motivation());
        application.setApplicationDate(request.applicationDate() != null ? request.applicationDate() : LocalDate.now());
        return ApplicationMapper.toResponse(applicationRepository.save(application));
    }

    @Override
    public ApplicationResponse update(Long id, ApplicationUpdateRequest request) {
        Application application = getApplication(id);
        application.setMotivation(request.motivation());
        application.setApplicationDate(request.applicationDate());
        return ApplicationMapper.toResponse(application);
    }

    @Override
    public void delete(Long id) {
        applicationRepository.delete(getApplication(id));
    }

    private Application getApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));
    }
}

