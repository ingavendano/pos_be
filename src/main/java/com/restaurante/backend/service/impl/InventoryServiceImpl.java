package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Inventory;
import com.restaurante.backend.domain.entity.Product;
import com.restaurante.backend.domain.entity.StockMovement;
import com.restaurante.backend.domain.entity.User;
import com.restaurante.backend.domain.entity.Warehouse;
import com.restaurante.backend.repository.InventoryRepository;
import com.restaurante.backend.repository.ProductRepository;
import com.restaurante.backend.repository.StockMovementRepository;
import com.restaurante.backend.repository.UserRepository;
import com.restaurante.backend.repository.WarehouseRepository;
import com.restaurante.backend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<Inventory> getStockForWarehouse(Long warehouseId) {
        return inventoryRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public List<Inventory> getLowStockAlerts(Long tenantId) {
        return inventoryRepository.findLowStockByTenantId(tenantId);
    }

    @Override
    @Transactional
    public Inventory adjustStock(Long warehouseId, Long productId, int quantity,
            StockMovement.MovementType type, String reason, Long userId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(
                        () -> new com.restaurante.backend.exception.ResourceNotFoundException("Bodega no encontrada"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new com.restaurante.backend.exception.ResourceNotFoundException(
                        "Producto no encontrado"));

        Inventory inventory = inventoryRepository
                .findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseGet(() -> Inventory.builder()
                        .warehouse(warehouse)
                        .product(product)
                        .quantity(0)
                        .build());

        if (type == StockMovement.MovementType.OUT) {
            if (inventory.getQuantity() - quantity < 0) {
                throw new com.restaurante.backend.exception.BusinessLogicException(
                        "Stock insuficiente para el producto: " + product.getName()
                                + " (Disponibles: " + inventory.getQuantity() + ")");
            }
        }

        int delta = (type == StockMovement.MovementType.OUT) ? -quantity : quantity;
        inventory.setQuantity(inventory.getQuantity() + delta);
        inventoryRepository.save(inventory);

        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        StockMovement movement = StockMovement.builder()
                .inventory(inventory)
                .movementType(type)
                .quantity(quantity)
                .reason(reason)
                .createdBy(user)
                .build();
        stockMovementRepository.save(movement);

        return inventory;
    }

    @Override
    public List<StockMovement> getMovements(Long warehouseId) {
        return stockMovementRepository.findByInventoryWarehouseIdOrderByCreatedAtDesc(warehouseId);
    }
}
