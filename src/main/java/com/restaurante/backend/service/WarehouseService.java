package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Warehouse;

import java.util.List;

public interface WarehouseService {
    List<Warehouse> getAllByTenant(Long tenantId);

    Warehouse create(Long tenantId, Long branchId, String name, String description);

    Warehouse update(Long warehouseId, String name, String description);

    void delete(Long warehouseId);
}
