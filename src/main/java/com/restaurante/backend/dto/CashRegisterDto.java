package com.restaurante.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

public class CashRegisterDto {

    @Data
    public static class OpenRequest {
        @NotNull(message = "El monto de apertura es obligatorio")
        @PositiveOrZero(message = "El monto de apertura debe ser igual o mayor a cero")
        private BigDecimal openingAmount;

        private String notes;
    }

    @Data
    public static class CloseRequest {
        @NotNull(message = "El monto de cierre es obligatorio")
        @PositiveOrZero(message = "El monto de cierre debe ser igual o mayor a cero")
        private BigDecimal closingAmount;

        private String notes;
    }

    @Data
    public static class Response {
        private Long id;
        private String status;

        // Opening
        private BigDecimal openingAmount;
        private String openedAt;
        private String openedByName;

        // Closing
        private BigDecimal closingAmount;
        private String closedAt;
        private String closedByName;

        // Sales totals
        private BigDecimal totalCash;
        private BigDecimal totalCard;
        private BigDecimal totalTransfer;
        private BigDecimal totalSales;
        private BigDecimal difference;

        private String notes;
        private String branchName;
    }
}
