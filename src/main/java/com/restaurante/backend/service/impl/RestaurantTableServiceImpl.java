package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Branch;
import com.restaurante.backend.domain.entity.RestaurantTable;
import com.restaurante.backend.repository.BranchRepository;
import com.restaurante.backend.repository.RestaurantTableRepository;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.RestaurantTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantTableServiceImpl implements RestaurantTableService {

    private final RestaurantTableRepository tableRepository;
    private final BranchRepository branchRepository;
    private final TenantSecurityService tenantSecurityService;

    @Override
    @Transactional
    public RestaurantTable createTable(Long branchId, RestaurantTable table) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Branch not found"));
        table.setBranch(branch);
        return tableRepository.save(table);
    }

    @Override
    @Transactional
    public RestaurantTable updateTable(Long id, RestaurantTable tableDetails) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Table not found"));
        
        // Verificar que la tabla pertenezca al tenant actual a través de su branch
        if (!table.getBranch().getTenant().getId().equals(tenantId)) {
            throw new com.restaurante.backend.exception.ResourceNotFoundException("Table not found");
        }

        if (tableDetails.getNumber() != null) {
            table.setNumber(tableDetails.getNumber());
        }
        if (tableDetails.getCapacity() != null) {
            table.setCapacity(tableDetails.getCapacity());
        }
        if (tableDetails.getPosX() != null) {
            table.setPosX(tableDetails.getPosX());
        }
        if (tableDetails.getPosY() != null) {
            table.setPosY(tableDetails.getPosY());
        }
        if (tableDetails.getStatus() != null) {
            table.setStatus(tableDetails.getStatus());
        }

        return tableRepository.save(table);
    }

    @Override
    public Optional<RestaurantTable> getTableById(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        return tableRepository.findById(id)
                .filter(table -> table.getBranch().getTenant().getId().equals(tenantId));
    }

    @Override
    public List<RestaurantTable> getTablesByBranchId(Long branchId) {
        return tableRepository.findByBranchId(branchId);
    }

    @Override
    @Transactional
    public RestaurantTable updateTableStatus(Long id, String status) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Table not found"));
        
        // Verificar que la tabla pertenezca al tenant actual a través de su branch
        if (!table.getBranch().getTenant().getId().equals(tenantId)) {
            throw new com.restaurante.backend.exception.ResourceNotFoundException("Table not found");
        }
        table.setStatus(status);
        return tableRepository.save(table);
    }

    @Override
    @Transactional
    public void deleteTable(Long id) {
        Long tenantId = tenantSecurityService.getCurrentTenantId();
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException("Table not found"));
        
        // Verificar que la tabla pertenezca al tenant actual a través de su branch
        if (!table.getBranch().getTenant().getId().equals(tenantId)) {
            throw new com.restaurante.backend.exception.ResourceNotFoundException("Table not found");
        }
        
        tableRepository.deleteById(id);
    }
}
