package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Branch;
import com.restaurante.backend.domain.entity.Tenant;
import com.restaurante.backend.domain.entity.Warehouse;
import com.restaurante.backend.repository.BranchRepository;
import com.restaurante.backend.repository.TenantRepository;
import com.restaurante.backend.repository.WarehouseRepository;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final BranchRepository branchRepository;
    private final TenantRepository tenantRepository;
    private final TenantSecurityService tenantSecurityService;

    @Override
    public List<Warehouse> getAllByTenant(Long tenantId) {
        return warehouseRepository.findByTenantId(tenantId);
    }

    @Override
    public Warehouse create(Long tenantId, Long branchId, String name, String description) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Tenant no encontrado"));
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                        "Sucursal no encontrada"));

        Warehouse warehouse = Warehouse.builder()
                .name(name)
                .description(description)
                .branch(branch)
                .tenant(tenant)
                .build();
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Warehouse update(Long warehouseId, String name, String description) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        Warehouse warehouse = warehouseRepository.findByIdAndTenantId(warehouseId, tenantId)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Bodega no encontrada"));
        warehouse.setName(name);
        warehouse.setDescription(description);
        return warehouseRepository.save(warehouse);
    }

    @Override
    public void delete(Long warehouseId) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        warehouseRepository.deleteByIdAndTenantId(warehouseId, tenantId);
    }
}
