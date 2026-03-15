package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Order;
import com.restaurante.backend.domain.entity.Product;
import com.restaurante.backend.domain.entity.RestaurantTable;
import com.restaurante.backend.dto.DashboardSummaryResponse;
import com.restaurante.backend.repository.OrderRepository;
import com.restaurante.backend.repository.ProductRepository;
import com.restaurante.backend.repository.RestaurantTableRepository;
import com.restaurante.backend.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final RestaurantTableRepository tableRepository;
    private final ProductRepository productRepository;
    private final com.restaurante.backend.service.InventoryService inventoryService;

    @Override
    public DashboardSummaryResponse getDashboardSummary(Long branchId, Long tenantId) {
        // 1. Get Tables Status
        List<RestaurantTable> tables = tableRepository.findByBranchId(branchId);
        int availableTables = (int) tables.stream().filter(t -> "AVAILABLE".equals(t.getStatus())).count();
        int occupiedTables = (int) tables.stream().filter(t -> "OCCUPIED".equals(t.getStatus())).count();

        // 2. Get Orders for Branch
        List<Order> allOrders = orderRepository.findByBranchIdOrderByCreatedAtDesc(branchId);
        LocalDate today = LocalDate.now();

        // Active pending orders
        int activeOrdersCount = (int) allOrders.stream()
                .filter(o -> "PENDING".equals(o.getStatus()))
                .count();

        // Today's Sales (PAID orders created today)
        BigDecimal todaySales = allOrders.stream()
                .filter(o -> "PAID".equals(o.getStatus()) && o.getCreatedAt().toLocalDate().isEqual(today))
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Recent Orders (last 10)
        List<Order> recentOrders = allOrders.stream()
                .limit(10)
                .collect(Collectors.toList());

        // 3. Low Stock Products (Source of Truth from Inventory)
        List<Product> lowStockProducts = inventoryService.getLowStockAlerts(tenantId).stream()
                .map(inv -> {
                    Product p = inv.getProduct();
                    // Ensure the quantity returned to dashboard is the actual inventory quantity
                    p.setQuantity(inv.getQuantity());
                    return p;
                })
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

        return DashboardSummaryResponse.builder()
                .todaySales(todaySales)
                .availableTables(availableTables)
                .occupiedTables(occupiedTables)
                .activeOrdersCount(activeOrdersCount)
                .lowStockProducts(lowStockProducts)
                .recentOrders(recentOrders)
                .build();
    }
}
