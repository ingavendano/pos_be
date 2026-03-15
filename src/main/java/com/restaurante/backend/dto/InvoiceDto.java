package com.restaurante.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class InvoiceDto {
    private Long id;
    private Long consecutiveNumber;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private String paymentMethod;
    private LocalDateTime issuedAt;

    // DTE Fields
    private String dteType;
    private String generationCode;
    private String controlNumber;
    private String receptionSello;
    private String dteStatus;
    private String rejectionReason;
}
