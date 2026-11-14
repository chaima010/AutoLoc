package com.awd.monolith.job.repository;

import com.awd.monolith.job.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l'entité {@link Category}.
 *
 * <p>Étend {@link JpaRepository} : CRUD complet, listes, tri, pagination et méthodes JPA
 * spécifiques ({@code flush}, {@code saveAndFlush}, {@code getReferenceById}…).
 *
 * <p>Méthodes dérivées :
 * <ul>
 *   <li>{@link #existsByNameIgnoreCase(String)} — unicité du nom à la création</li>
 *   <li>{@link #existsByNameIgnoreCaseAndIdNot(String, Long)} — unicité à la mise à jour</li>
 * </ul>
 *
 * <p>Convention AWD : le nom de chaque interface repository commence par {@code I}.
 */
public interface ICategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Vérifie qu'aucune catégorie n'utilise déjà ce nom (insensible à la casse).
     * Utilisé à la création pour garantir l'unicité.
     *
     * @param name le nom de la catégorie à vérifier
     * @return {@code true} si une catégorie avec ce nom existe déjà
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Vérifie qu'aucune autre catégorie (différente de {@code id}) n'utilise ce nom.
     * Utilisé à la mise à jour pour permettre de conserver le même nom.
     *
     * @param name le nom de la catégorie à vérifier
     * @param id   l'identifiant de la catégorie à exclure de la vérification
     * @return {@code true} si une autre catégorie avec ce nom existe déjà
     */
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}

