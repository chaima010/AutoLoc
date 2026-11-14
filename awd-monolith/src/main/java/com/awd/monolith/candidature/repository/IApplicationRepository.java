package com.awd.monolith.candidature.repository;

import com.awd.monolith.candidature.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Application} (candidature).
 *
 * <p>Étend {@link JpaRepository} : CRUD complet, listes, tri, pagination et méthodes JPA
 * spécifiques ({@code flush}, {@code saveAndFlush}, {@code getReferenceById}…).
 *
 * <p><strong>Attention aux suppressions en lot :</strong> les méthodes
 * {@code deleteAllInBatch} et {@code deleteAllByIdInBatch} (héritées de {@code JpaRepository})
 * contournent le contexte de persistance — la cascade et l'orphanRemoval ne s'appliquent pas.
 * Préférer {@code deleteById} ou {@code delete} pour respecter le cycle de vie JPA.
 *
 * <p>La requête JPQL {@link #search(Long, Long)} effectue des {@code JOIN FETCH} sur le candidat,
 * le job et sa catégorie pour éviter le problème N+1.
 *
 * <p>Convention AWD : le nom de chaque interface repository commence par {@code I}.
 */
public interface IApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * Recherche des candidatures avec des filtres optionnels.
     * Un paramètre {@code null} signifie « pas de filtre sur ce critère ».
     * Le candidat, le job et sa catégorie sont chargés en une seule requête SQL (JOIN FETCH).
     *
     * @param candidateId identifiant du candidat, ou {@code null} pour tous
     * @param jobId       identifiant du job, ou {@code null} pour tous
     * @return liste des candidatures correspondant aux critères, triées par date décroissante
     */
    @Query("""
            select a from Application a
            join fetch a.candidate c
            join fetch a.job j
            join fetch j.category
            where (:candidateId is null or c.id = :candidateId)
              and (:jobId is null or j.id = :jobId)
            order by a.applicationDate desc, a.id desc
            """)
    List<Application> search(@Param("candidateId") Long candidateId, @Param("jobId") Long jobId);

    /**
     * Vérifie qu'une candidature existe déjà pour ce couple candidat/job.
     * Garantit la règle métier : un candidat ne peut postuler qu'une seule fois au même job.
     *
     * @param candidateId identifiant du candidat
     * @param jobId       identifiant du job
     * @return {@code true} si la candidature existe déjà
     */
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);

    /**
     * Vérifie qu'un candidat a au moins une candidature.
     * Utilisé avant la suppression d'un candidat (règle : 409 Conflict si candidatures actives).
     *
     * @param candidateId identifiant du candidat
     * @return {@code true} si le candidat a au moins une candidature
     */
    boolean existsByCandidateId(Long candidateId);

    /**
     * Vérifie qu'un job a au moins une candidature.
     * Utilisé avant la suppression d'un job (règle : 409 Conflict si candidatures actives).
     *
     * @param jobId identifiant du job
     * @return {@code true} si le job a au moins une candidature
     */
    boolean existsByJobId(Long jobId);
}

