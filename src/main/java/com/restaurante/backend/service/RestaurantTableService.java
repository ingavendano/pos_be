package com.restaurante.backend.service;

import com.restaurante.backend.domain.entity.RestaurantTable;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableService {
    RestaurantTable createTable(Long branchId, RestaurantTable table);

    RestaurantTable updateTable(Long id, RestaurantTable tableDetails);

    Optional<RestaurantTable> getTableById(Long id);

    List<RestaurantTable> getTablesByBranchId(Long branchId);

    RestaurantTable updateTableStatus(Long id, String status);

    void deleteTable(Long id);
}
