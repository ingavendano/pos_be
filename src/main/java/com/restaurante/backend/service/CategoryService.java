package com.restaurante.backend.service;

import com.restaurante.backend.domain.dto.CategoryDTO;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryDTO createCategory(Long tenantId, CategoryDTO categoryDTO);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    Optional<CategoryDTO> getCategoryById(Long id);

    List<CategoryDTO> getCategoriesByTenantId(Long tenantId);

    void deleteCategory(Long id);
}
