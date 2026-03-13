package com.restaurante.backend.service;

import com.restaurante.backend.dto.SalesReportResponse;
import com.restaurante.backend.dto.SalesReportResponse.*;
import com.restaurante.backend.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

        private final InvoiceRepository invoiceRepository;
        private final EntityManager em;

        @Transactional(readOnly = true)
        public SalesReportResponse getSalesReport(Long tenantId, LocalDate from, LocalDate to) {
                LocalDateTime fromDt = from.atStartOfDay();
                LocalDateTime toDt = to.plusDays(1).atStartOfDay().minusNanos(1);

                // ── KPIs ─────────────────────────────────────────────
                BigDecimal totalRevenue = invoiceRepository
                                .sumTotalByTenantAndDateRange(tenantId, fromDt, toDt)
                                .orElse(BigDecimal.ZERO);

                BigDecimal totalTax = invoiceRepository
                                .sumTaxByTenantAndDateRange(tenantId, fromDt, toDt)
                                .orElse(BigDecimal.ZERO);

                BigDecimal totalSubtotal = invoiceRepository
                                .sumSubtotalByTenantAndDateRange(tenantId, fromDt, toDt)
                                .orElse(BigDecimal.ZERO);

                Long totalInvoices = invoiceRepository.countByTenantAndDateRange(tenantId, fromDt, toDt);

                BigDecimal averageTicket = totalInvoices > 0
                                ? totalRevenue.divide(BigDecimal.valueOf(totalInvoices), 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;

                // ── Daily Sales ───────────────────────────────────────
                List<DailySales> dailySales = invoiceRepository
                                .dailyRevenue(tenantId, fromDt, toDt)
                                .stream()
                                .map(row -> DailySales.builder()
                                                .date(row[0].toString())
                                                .revenue((BigDecimal) row[1])
                                                .invoiceCount(((Number) row[2]).longValue())
                                                .build())
                                .collect(Collectors.toList());

                // ── Hourly Breakdown ──────────────────────────────────
                Map<Integer, BigDecimal> revenueByHour = new HashMap<>();
                invoiceRepository.revenueByHour(tenantId, fromDt, toDt)
                                .forEach(row -> {
                                        int hour = ((Number) row[0]).intValue();
                                        BigDecimal rev = row[1] instanceof BigDecimal
                                                        ? (BigDecimal) row[1]
                                                        : BigDecimal.valueOf(((Number) row[1]).doubleValue());
                                        revenueByHour.put(hour, rev);
                                });

                // ── Payment Methods ───────────────────────────────────
                List<Object[]> pmRows = invoiceRepository.revenueByPaymentMethod(tenantId, fromDt, toDt);
                BigDecimal grandTotal = totalRevenue.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ONE : totalRevenue;

                List<PaymentMethodBreakdown> paymentMethods = pmRows.stream()
                                .map(row -> {
                                        BigDecimal methodTotal = (BigDecimal) row[2];
                                        double pct = methodTotal.divide(grandTotal, 4, RoundingMode.HALF_UP)
                                                        .multiply(BigDecimal.valueOf(100)).doubleValue();
                                        return PaymentMethodBreakdown.builder()
                                                        .method((String) row[0])
                                                        .count(((Number) row[1]).longValue())
                                                        .total(methodTotal)
                                                        .percentage(Math.round(pct * 10.0) / 10.0)
                                                        .build();
                                }).collect(Collectors.toList());

                // ── Top Products ─────────────────────────────────────
                @SuppressWarnings("unchecked")
                List<Object[]> productRows = em.createQuery(
                                "SELECT oi.product.name, SUM(oi.quantity), SUM(oi.subtotal) " +
                                                "FROM OrderItem oi " +
                                                "WHERE oi.order.branch.tenant.id = :tenantId " +
                                                "AND oi.order.createdAt BETWEEN :from AND :to " +
                                                "GROUP BY oi.product.name " +
                                                "ORDER BY SUM(oi.quantity) DESC")
                                .setParameter("tenantId", tenantId)
                                .setParameter("from", fromDt)
                                .setParameter("to", toDt)
                                .setMaxResults(10)
                                .getResultList();

                List<ProductSales> topProducts = productRows.stream()
                                .map(row -> ProductSales.builder()
                                                .productName((String) row[0])
                                                .quantitySold(((Number) row[1]).longValue())
                                                .revenue((BigDecimal) row[2])
                                                .build())
                                .collect(Collectors.toList());

                // ── Waiter Performance ────────────────────────────────
                List<WaiterSales> topWaiters = invoiceRepository
                                .revenueByWaiter(tenantId, fromDt, toDt)
                                .stream()
                                .map(row -> WaiterSales.builder()
                                                .waiterName((String) row[0])
                                                .ordersServed(((Number) row[1]).longValue())
                                                .revenue((BigDecimal) row[2])
                                                .build())
                                .collect(Collectors.toList());

                return SalesReportResponse.builder()
                                .totalRevenue(totalRevenue)
                                .totalTax(totalTax)
                                .totalSubtotal(totalSubtotal)
                                .totalInvoices(totalInvoices)
                                .averageTicket(averageTicket)
                                .dailySales(dailySales)
                                .revenueByHour(revenueByHour)
                                .topProducts(topProducts)
                                .paymentMethods(paymentMethods)
                                .topWaiters(topWaiters)
                                .build();
        }
}
