package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    List<Tax> findByTenantId(Long tenantId);

    List<Tax> findByTenantIdAndIsActiveTrue(Long tenantId);

    Optional<Tax> findByIdAndTenantId(Long id, Long tenantId);

    void deleteByIdAndTenantId(Long id, Long tenantId);
}
