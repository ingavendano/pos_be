package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.dto.CategoryDTO;
import com.restaurante.backend.domain.entity.Category;
import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.repository.CategoryRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TenantRepository tenantRepository;
    private final TenantSecurityService tenantSecurityService;

    @Override
    @Transactional
    public CategoryDTO createCategory(Long tenantId, CategoryDTO categoryDTO) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Tenant not found"));
        
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .tenant(tenant)
                .build();
                
        return mapToDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        Category category = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Category not found"));

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        return mapToDTO(categoryRepository.save(category));
    }

    @Override
    public Optional<CategoryDTO> getCategoryById(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        return categoryRepository.findByIdAndTenantId(id, tenantId).map(this::mapToDTO);
    }

    @Override
    public List<CategoryDTO> getCategoriesByTenantId(Long tenantId) {
        return categoryRepository.findByTenantId(tenantId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        categoryRepository.deleteByIdAndTenantId(id, tenantId);
    }

    private CategoryDTO mapToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .tenantId(category.getTenant().getId())
                .build();
    }
}
