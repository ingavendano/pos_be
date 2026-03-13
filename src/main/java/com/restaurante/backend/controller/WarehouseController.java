package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Warehouse;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final TenantSecurityService tenantSecurity;

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<Warehouse>> getAll(@PathVariable Long tenantId) {
        tenantSecurity.verifyTenantAccess(tenantId);
        return ResponseEntity.ok(warehouseService.getAllByTenant(tenantId));
    }

    @PostMapping("/tenant/{tenantId}")
    public ResponseEntity<Warehouse> create(@PathVariable Long tenantId,
            @RequestBody Map<String, Object> body) {
        tenantSecurity.verifyTenantAccess(tenantId);
        Long branchId = Long.parseLong(body.get("branchId").toString());
        String name = body.get("name").toString();
        
        Object descObj = body.get("description");
        String description = descObj != null ? descObj.toString() : null;
        
        return ResponseEntity.ok(warehouseService.create(tenantId, branchId, name, description));
    }

    @PutMapping("/{warehouseId}")
    public ResponseEntity<Warehouse> update(@PathVariable Long warehouseId,
            @RequestBody Map<String, Object> body) {
        String name = body.get("name").toString();
        
        Object descObj = body.get("description");
        String description = descObj != null ? descObj.toString() : null;
        
        return ResponseEntity.ok(warehouseService.update(warehouseId, name, description));
    }

    @DeleteMapping("/{warehouseId}")
    public ResponseEntity<Void> delete(@PathVariable Long warehouseId) {
        warehouseService.delete(warehouseId);
        return ResponseEntity.noContent().build();
    }
}
