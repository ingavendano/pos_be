package com.restaurante.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cash_registers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Status: OPEN | CLOSED
     */
    @Column(nullable = false)
    @Builder.Default
    private String status = "OPEN";

    // ── Opening ───────────────────────────────────────────────

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal openingAmount; // Fondo inicial en caja

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime openedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opened_by_id", nullable = false)
    private User openedBy; // Cajero que abrió la caja

    // ── Closing ───────────────────────────────────────────────

    @Column(precision = 10, scale = 2)
    private BigDecimal closingAmount; // Dinero contado al cierre

    @Column
    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_id")
    private User closedBy; // Cajero que cerró la caja

    // ── Totals calculated at closing ──────────────────────────

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalCash = BigDecimal.ZERO; // Ventas en efectivo en el turno

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalCard = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalTransfer = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalSales = BigDecimal.ZERO; // Suma total de ventas

    /**
     * Diferencia = closingAmount - (openingAmount + totalCash)
     * Positivo = sobrante, Negativo = faltante
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal difference;

    @Column(columnDefinition = "TEXT")
    private String notes; // Observaciones del cajero

    // ── Relations ─────────────────────────────────────────────

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
}
