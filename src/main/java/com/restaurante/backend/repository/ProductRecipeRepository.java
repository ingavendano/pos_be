package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.ProductRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRecipeRepository extends JpaRepository<ProductRecipe, Long> {
    List<ProductRecipe> findByProductId(Long productId);
    List<ProductRecipe> findByTenantId(Long tenantId);
    void deleteByProductId(Long productId);
}
