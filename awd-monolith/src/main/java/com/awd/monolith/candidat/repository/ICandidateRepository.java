package com.awd.monolith.candidat.repository;

import com.awd.monolith.candidat.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ICandidateRepository extends JpaRepository<Candidate, Long> {


    boolean existsByEmailIgnoreCase(String email);


    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}

