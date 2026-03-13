package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category createCategory(Long tenantId, Category category);

    Category updateCategory(Long id, Category categoryDetails);

    Optional<Category> getCategoryById(Long id);

    List<Category> getCategoriesByTenantId(Long tenantId);

    void deleteCategory(Long id);
}
