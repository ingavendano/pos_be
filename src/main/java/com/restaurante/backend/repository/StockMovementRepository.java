package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByInventoryWarehouseIdOrderByCreatedAtDesc(Long warehouseId);

    List<StockMovement> findByInventoryIdOrderByCreatedAtDesc(Long inventoryId);
}
