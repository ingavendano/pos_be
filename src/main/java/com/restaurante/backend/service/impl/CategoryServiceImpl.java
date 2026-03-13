package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Category;
import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.repository.CategoryRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;

    @Override
    @Transactional
    public Category createCategory(Long tenantId, Category category) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Tenant not found"));
        category.setTenant(tenant);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Category not found"));

        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());

        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getCategoriesByTenantId(Long tenantId) {
        return categoryRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
