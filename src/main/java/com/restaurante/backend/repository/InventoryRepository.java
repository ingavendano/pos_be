package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByWarehouseId(Long warehouseId);

    Optional<Inventory> findByWarehouseIdAndProductId(Long warehouseId, Long productId);

    @Query("SELECT i FROM Inventory i WHERE i.warehouse.tenant.id = :tenantId AND i.quantity <= i.product.minStock")
    List<Inventory> findLowStockByTenantId(@Param("tenantId") Long tenantId);
}
