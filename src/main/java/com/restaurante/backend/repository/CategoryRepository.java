package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByTenantId(Long tenantId);

    Optional<Category> findByIdAndTenantId(Long id, Long tenantId);

    void deleteByIdAndTenantId(Long id, Long tenantId);
}
