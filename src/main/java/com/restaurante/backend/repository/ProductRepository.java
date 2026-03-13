package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Eagerly join category and tenant to avoid N+1 when listing products
    @EntityGraph(attributePaths = { "category", "tenant" })
    List<Product> findByTenantId(Long tenantId);

    @EntityGraph(attributePaths = { "category", "tenant" })
    List<Product> findByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = { "category", "tenant" })
    List<Product> findByTenantIdAndIsAvailableTrue(Long tenantId);
}
