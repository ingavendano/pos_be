package com.restaurante.backend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfitabilityReportResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalCogs; // Cost of Goods Sold
    private BigDecimal totalExpenses;
    private BigDecimal grossProfit; // Revenue - COGS
    private BigDecimal netProfit; // Gross Profit - Expenses
    private BigDecimal profitMarginPercentage;
    
    // Breakdown by category
    private Map<String, BigDecimal> expensesByCategory;
}
