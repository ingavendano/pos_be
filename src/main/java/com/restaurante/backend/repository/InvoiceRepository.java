package com.restaurante.backend.repository;

import com.restaurante.backend.domain.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

        Optional<Invoice> findByOrderId(Long orderId);

        // ── KPI Aggregations ──────────────────────────────────────

        @Query("SELECT SUM(i.total) FROM Invoice i " +
                        "WHERE i.order.branch.tenant.id = :tenantId " +
                        "AND i.issuedAt BETWEEN :from AND :to")
        Optional<BigDecimal> sumTotalByTenantAndDateRange(
                        @Param("tenantId") Long tenantId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        @Query("SELECT SUM(i.tax) FROM Invoice i " +
                        "WHERE i.order.branch.tenant.id = :tenantId " +
                        "AND i.issuedAt BETWEEN :from AND :to")
        Optional<BigDecimal> sumTaxByTenantAndDateRange(
                        @Param("tenantId") Long tenantId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        @Query("SELECT SUM(i.subtotal) FROM Invoice i " +
                        "WHERE i.order.branch.tenant.id = :tenantId " +
                        "AND i.issuedAt BETWEEN :from AND :to")
        Optional<BigDecimal> sumSubtotalByTenantAndDateRange(
                        @Param("tenantId") Long tenantId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        @Query("SELECT COUNT(i) FROM Invoice i " +
                        "WHERE i.order.branch.tenant.id = :tenantId " +
                        "AND i.issuedAt BETWEEN :from AND :to")
        Long countByTenantAndDateRange(
                        @Param("tenantId") Long tenantId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        // ── Daily Breakdown ───────────────────────────────────────

        @Query(value = "SELECT CAST(i.issued_at AS DATE) AS day, " +
                        "SUM(i.total) AS revenue, COUNT(i.id) AS cnt " +
                        "FROM invoices i " +
                        "JOIN orders o ON i.order_id = o.id " +
                        "JOIN branches b ON o.branch_id = b.id " +
                        "WHERE b.tenant_id = :tenantId " +
                        "AND i.issued_at BETWEEN :from AND :to " +
                        "GROUP BY CAST(i.issued_at AS DATE) " +
                        "ORDER BY day ASC", nativeQuery = true)
        List<Object[]> dailyRevenue(
                        @Param("tenantId") Long tenantId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        // ── Hourly Breakdown ──────────────────────────────────────

        @Query(value = "SELECT EXTRACT(HOUR FROM i.issued_at) AS hour, SUM(i.total) AS revenue " +
                        "FROM invoices i " +
                        "JOIN orders o ON i.order_id = o.id " +
                        "JOIN branches b ON o.branch_id = b.id " +
                        "WHERE b.tenant_id = :tenantId " +
                        "AND i.issued_at BETWEEN :from AND :to " +
                        "GROUP BY EXTRACT(HOUR FROM i.issued_at) " +
                        "ORDER BY hour ASC", nativeQuery = true)
        List<Object[]> revenueByHour(
                        @Param("tenantId") Long tenantId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        // ── Payment Method Breakdown ──────────────────────────────

        @Query("SELECT i.paymentMethod, COUNT(i), SUM(i.total) " +
                        "FROM Invoice i " +
                        "WHERE i.order.branch.tenant.id = :tenantId " +
                        "AND i.issuedAt BETWEEN :from AND :to " +
                        "GROUP BY i.paymentMethod")
        List<Object[]> revenueByPaymentMethod(
                        @Param("tenantId") Long tenantId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        // ── Waiter Performance ────────────────────────────────────

        @Query("SELECT i.order.user.name, COUNT(i), SUM(i.total) " +
                        "FROM Invoice i " +
                        "WHERE i.order.branch.tenant.id = :tenantId " +
                        "AND i.issuedAt BETWEEN :from AND :to " +
                        "GROUP BY i.order.user.name " +
                        "ORDER BY SUM(i.total) DESC")
        List<Object[]> revenueByWaiter(
                        @Param("tenantId") Long tenantId,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);

        @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i " +
                        "WHERE i.order.branch.id = :branchId " +
                        "AND i.paymentMethod = :method " +
                        "AND i.issuedAt BETWEEN :from AND :to")
        BigDecimal sumByBranchAndPaymentMethodAndDateRange(
                        @Param("branchId") Long branchId,
                        @Param("method") String method,
                        @Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to);
}
