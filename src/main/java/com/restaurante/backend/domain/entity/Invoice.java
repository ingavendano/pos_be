package com.restaurante.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false)
    private String paymentMethod; // CASH, CARD, TRANSFER

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime issuedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    // Facturación Electrónica (DTE El Salvador)
    @Column(length = 2)
    private String dteType; // 01=Factura, 03=CCF

    @Column(length = 36)
    private String generationCode; // UUID

    @Column(length = 31)
    private String controlNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Tenant tenant;

    @Column(name = "consecutive_number", updatable = false)
    private Long consecutiveNumber;

    @Column(length = 40)
    private String receptionSello;

    @Column(length = 20)
    @Builder.Default
    private String dteStatus = "PENDING";

    @Column(columnDefinition = "TEXT")
    private String dteJson;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(length = 40)
    private String invalidationSello;
}
