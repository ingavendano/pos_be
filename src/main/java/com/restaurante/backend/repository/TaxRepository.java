package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxRepository extends JpaRepository<Tax, Long> {
    List<Tax> findByTenantId(Long tenantId);

    List<Tax> findByTenantIdAndIsActiveTrue(Long tenantId);
}
