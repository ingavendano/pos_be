package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
    List<RestaurantTable> findByBranchId(Long branchId);

    List<RestaurantTable> findByBranchIdAndStatus(Long branchId, String status);

    Optional<RestaurantTable> findByBranchIdAndNumber(Long branchId, Integer number);
}
