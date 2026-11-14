package com.awd.monolith.job.repository;

import com.awd.monolith.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Job}.
 *
 * <p>Étend {@link JpaRepository} : CRUD complet, listes, tri, pagination et méthodes JPA
 * spécifiques ({@code flush}, {@code saveAndFlush}, {@code getReferenceById}…).
 *
 * <p>La requête JPQL {@link #search(Boolean, Long)} effectue un {@code JOIN FETCH} sur la
 * catégorie pour éviter le problème N+1 : une seule requête SQL charge les jobs et leur catégorie.
 *
 * <p>Convention AWD : le nom de chaque interface repository commence par {@code I}.
 */
public interface IJobRepository extends JpaRepository<Job, Long> {

    /**
     * Recherche des offres d'emploi avec des filtres optionnels.
     * Un paramètre {@code null} signifie « pas de filtre sur ce critère ».
     * La catégorie est chargée en même temps (JOIN FETCH) pour éviter les requêtes N+1.
     *
     * @param available  {@code true} = offres ouvertes, {@code false} = fermées, {@code null} = toutes
     * @param categoryId identifiant de la catégorie filtrante, ou {@code null} pour toutes les catégories
     * @return liste des offres correspondant aux critères, triées par date décroissante
     */
    @Query("""
            select j from Job j join fetch j.category c
            where (:available is null or j.available = :available)
              and (:categoryId is null or c.id = :categoryId)
            order by j.date desc, j.id desc
            """)
    List<Job> search(@Param("available") Boolean available, @Param("categoryId") Long categoryId);

    /**
     * Vérifie qu'au moins un job appartient à la catégorie donnée.
     * Utilisé avant la suppression d'une catégorie pour détecter les conflits.
     *
     * @param categoryId identifiant de la catégorie
     * @return {@code true} si la catégorie contient au moins un job
     */
    boolean existsByCategoryId(Long categoryId);
}

