package com.restaurante.backend.service.impl;

import com.restaurante.backend.domain.entity.Expense;
import com.restaurante.backend.dto.ProfitabilityReportResponse;
import com.restaurante.backend.repository.ExpenseRepository;
import com.restaurante.backend.repository.InvoiceRepository;
import com.restaurante.backend.repository.OrderRepository;
import com.restaurante.backend.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportingServiceImpl implements ReportingService {

    private final InvoiceRepository invoiceRepository;
    private final ExpenseRepository expenseRepository;
    private final OrderRepository orderRepository;

    @Override
    public ProfitabilityReportResponse getProfitabilityReport(Long branchId, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);

        // 1. Total Revenue
        // We'll need a new query in InvoiceRepository or use existing one if we can adjust it for branch
        BigDecimal totalRevenue = invoiceRepository.sumTotalByBranchAndDateRange(branchId, startDateTime, endDateTime)
                .orElse(BigDecimal.ZERO);

        // 2. Total Expenses
        BigDecimal totalExpenses = expenseRepository.sumAmountByBranchAndDateRange(branchId, start, end)
                .orElse(BigDecimal.ZERO);

        Map<String, BigDecimal> expensesByCategory = expenseRepository.sumAmountByCategoryAndBranchAndDateRange(branchId, start, end)
                .stream()
                .collect(Collectors.toMap(
                        row -> ((com.restaurante.backend.domain.entity.ExpenseCategory) row[0]).name(),
                        row -> (BigDecimal) row[1]
                ));

        // 3. Total COGS
        // We need all items from orders that have an invoice in this period
        // For simplicity, we can query orders by branch and status PAID/READY in the date range
        // Better: join with invoices to be precise about WHICH orders contributed to revenue in this period
        BigDecimal totalCogs = calculateCogsForBranchAndPeriod(branchId, startDateTime, endDateTime);

        BigDecimal grossProfit = totalRevenue.subtract(totalCogs);
        BigDecimal netProfit = grossProfit.subtract(totalExpenses);

        BigDecimal profitMarginPercentage = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMarginPercentage = netProfit.multiply(new BigDecimal("100"))
                    .divide(totalRevenue, 2, RoundingMode.HALF_UP);
        }

        return ProfitabilityReportResponse.builder()
                .totalRevenue(totalRevenue)
                .totalCogs(totalCogs)
                .totalExpenses(totalExpenses)
                .grossProfit(grossProfit)
                .netProfit(netProfit)
                .profitMarginPercentage(profitMarginPercentage)
                .expensesByCategory(expensesByCategory)
                .build();
    }

    private BigDecimal calculateCogsForBranchAndPeriod(Long branchId, LocalDateTime start, LocalDateTime end) {
        // This is a simplified approach: sum (quantity * productionCost) for all items in invoiced orders
        // A direct JPQL query would be more efficient
        return orderRepository.calculateCogsByBranchAndDateRange(branchId, start, end)
                .orElse(BigDecimal.ZERO);
    }
}
