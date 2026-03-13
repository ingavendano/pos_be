package com.restaurante.backend.dto;

import com.restaurante.backend.domain.entity.Order;
import com.restaurante.backend.domain.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardSummaryResponse {
    private BigDecimal todaySales;
    private int availableTables;
    private int occupiedTables;
    private int activeOrdersCount;
    private List<Product> lowStockProducts;
    private List<Order> recentOrders;
}
