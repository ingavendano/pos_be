package com.restaurante.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class SalesReportResponse {

    // ── Summary KPIs ─────────────────────────────────────────
    private BigDecimal totalRevenue;
    private BigDecimal totalTax;
    private BigDecimal totalSubtotal;
    private Long totalInvoices;
    private BigDecimal averageTicket;

    // ── Revenue by Day ────────────────────────────────────────
    private List<DailySales> dailySales;

    // ── Revenue by Hour (heatmap source) ─────────────────────
    private Map<Integer, BigDecimal> revenueByHour;

    // ── Top Products ──────────────────────────────────────────
    private List<ProductSales> topProducts;

    // ── Payment Method Breakdown ──────────────────────────────
    private List<PaymentMethodBreakdown> paymentMethods;

    // ── Waiter Performance ────────────────────────────────────
    private List<WaiterSales> topWaiters;

    // ─────────────────────────────────────────────────────────

    @Data
    @Builder
    public static class DailySales {
        private String date; // yyyy-MM-dd
        private BigDecimal revenue;
        private Long invoiceCount;
    }

    @Data
    @Builder
    public static class ProductSales {
        private String productName;
        private Long quantitySold;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    public static class PaymentMethodBreakdown {
        private String method;
        private Long count;
        private BigDecimal total;
        private Double percentage;
    }

    @Data
    @Builder
    public static class WaiterSales {
        private String waiterName;
        private Long ordersServed;
        private BigDecimal revenue;
    }
}
