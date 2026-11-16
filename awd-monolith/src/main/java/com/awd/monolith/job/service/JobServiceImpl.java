package com.awd.monolith.job.service;

import com.awd.monolith.candidature.repository.IApplicationRepository;
import com.awd.monolith.common.exception.ConflictException;
import com.awd.monolith.common.exception.ResourceNotFoundException;
import com.awd.monolith.job.dto.JobRequest;
import com.awd.monolith.job.dto.JobResponse;
import com.awd.monolith.job.entity.Job;
import com.awd.monolith.job.mapper.JobMapper;
import com.awd.monolith.job.repository.IJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du service métier des offres d'emploi (jobs).
 */
@Service
@Transactional
@RequiredArgsConstructor
public class JobServiceImpl implements IJobService {

    private final IJobRepository jobRepository;
    private final ICategoryService categoryService;
    // Monolith coupling: the job module reads the candidature table directly.
    private final IApplicationRepository applicationRepository;

    /** Lists jobs, optionally filtered by availability and/or category. */
    @Override
    @Transactional(readOnly = true)
    public List<JobResponse> search(Boolean available, Long categoryId) {
        if (categoryId != null) {
            categoryService.getCategory(categoryId); // 404 if the category does not exist
        }
        return jobRepository.search(available, categoryId).stream().map(JobMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponse findById(Long id) {
        return JobMapper.toResponse(getJob(id));
    }

    @Override
    public JobResponse create(JobRequest request) {
        Job job = new Job();
        JobMapper.updateEntity(job, request, categoryService.getCategory(request.categoryId()));
        return JobMapper.toResponse(jobRepository.save(job));
    }

    @Override
    public JobResponse update(Long id, JobRequest request) {
        Job job = getJob(id);
        JobMapper.updateEntity(job, request, categoryService.getCategory(request.categoryId()));
        return JobMapper.toResponse(job);
    }

    /** Opens or closes a job without sending the whole object. */
    @Override
    public JobResponse setAvailability(Long id, boolean available) {
        Job job = getJob(id);
        job.setAvailable(available);
        return JobMapper.toResponse(job);
    }

    @Override
    public void delete(Long id) {
        Job job = getJob(id);
        if (applicationRepository.existsByJobId(id)) {
            throw new ConflictException("Job " + id + " has applications; delete them first");
        }
        jobRepository.delete(job);
    }

    /** Also used by the candidature module (in-process call inside the monolith). */
    @Override
    public Job getJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", id));
    }
}

