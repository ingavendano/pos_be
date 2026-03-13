package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.Inventory;
import com.restaurante.backend.domain.entity.StockMovement;

import java.util.List;

public interface InventoryService {
    List<Inventory> getStockForWarehouse(Long warehouseId);

    List<Inventory> getLowStockAlerts(Long tenantId);

    Inventory adjustStock(Long warehouseId, Long productId, int quantity,
            StockMovement.MovementType type, String reason, Long userId);

    List<StockMovement> getMovements(Long warehouseId);
}
