package com.restaurante.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_taxes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceTax {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id", nullable = false)
    private Tax tax;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal appliedPercentage; // Historic percentage at the time of invoice

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountCalculated; // Actual money amount for this tax
}
