package com.awd.monolith.job.service;

import com.awd.monolith.job.dto.JobRequest;
import com.awd.monolith.job.dto.JobResponse;
import com.awd.monolith.job.entity.Job;

import java.util.List;

/**
 * Interface de service pour la gestion métier des offres d'emploi (jobs).
 */
public interface IJobService {

    List<JobResponse> search(Boolean available, Long categoryId);

    JobResponse findById(Long id);

    JobResponse create(JobRequest request);

    JobResponse update(Long id, JobRequest request);

    JobResponse setAvailability(Long id, boolean available);

    void delete(Long id);

    default void deleteById(Long id) {
        delete(id);
    }

    Job getJob(Long id);
}

