package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    List<Warehouse> findByTenantId(Long tenantId);

    List<Warehouse> findByBranchId(Long branchId);

    java.util.Optional<Warehouse> findFirstByBranchIdAndIsDefaultTrue(Long branchId);
}
