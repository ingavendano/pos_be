package com.restaurante.backend.controller;

import com.restaurante.backend.domain.entity.Inventory;
import com.restaurante.backend.domain.entity.StockMovement;
import com.restaurante.backend.security.TenantSecurityService;
import com.restaurante.backend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final TenantSecurityService tenantSecurity;

    /** List all inventory rows for a warehouse */
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<Inventory>> getStock(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getStockForWarehouse(warehouseId));
    }

    /** List all items below minimum stock for a tenant */
    @GetMapping("/alerts/tenant/{tenantId}")
    public ResponseEntity<List<Inventory>> getLowStock(@PathVariable Long tenantId) {
        tenantSecurity.verifyTenantAccess(tenantId);
        return ResponseEntity.ok(inventoryService.getLowStockAlerts(tenantId));
    }

    /** Adjust stock (IN / OUT / ADJUSTMENT) */
    @PostMapping("/warehouse/{warehouseId}/adjust")
    public ResponseEntity<Inventory> adjust(@PathVariable Long warehouseId,
            @RequestBody Map<String, Object> body) {
        Long productId = Long.parseLong(body.get("productId").toString());
        int quantity = Integer.parseInt(body.get("quantity").toString());
        StockMovement.MovementType type = StockMovement.MovementType.valueOf(body.get("type").toString());
        String reason = body.containsKey("reason") ? body.get("reason").toString() : null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // userId resolved via currentUser from auth principal name - pass null for now,
        // service will handle gracefully
        Long userId = null;
        if (body.containsKey("userId")) {
            userId = Long.parseLong(body.get("userId").toString());
        }

        return ResponseEntity.ok(inventoryService.adjustStock(warehouseId, productId, quantity, type, reason, userId));
    }

    /** List all movements for a warehouse */
    @GetMapping("/movements/warehouse/{warehouseId}")
    public ResponseEntity<List<StockMovement>> getMovements(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(inventoryService.getMovements(warehouseId));
    }
}
