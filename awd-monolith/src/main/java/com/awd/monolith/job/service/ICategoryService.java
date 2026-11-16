package com.awd.monolith.job.service;

import com.awd.monolith.job.dto.CategoryRequest;
import com.awd.monolith.job.dto.CategoryResponse;
import com.awd.monolith.job.entity.Category;

import java.util.List;

/**
 * Interface de service pour la gestion métier des catégories de postes.
 */
public interface ICategoryService {

    List<CategoryResponse> findAll();

    CategoryResponse findById(Long id);

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);

    default void deleteById(Long id) {
        delete(id);
    }

    Category getCategory(Long id);
}

