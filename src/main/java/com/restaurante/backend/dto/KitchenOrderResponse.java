package com.restaurante.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Lightweight DTO designed for the Kitchen Display screen.
 * Contains only data relevant to kitchen staff.
 */
@Data
@Builder
public class KitchenOrderResponse {

    private Long id;
    private Long consecutiveNumber;
    private String tableNumber; // e.g., "Mesa 5"
    private String waiterName; // who placed the order
    private String status; // PENDING | PREPARING | READY
    private LocalDateTime createdAt;
    private Long minutesElapsed;
    private List<KitchenItemResponse> items;

    @Data
    @Builder
    public static class KitchenItemResponse {
        private Long id;
        private String productName;
        private Integer quantity;
        private String notes;
        private String status; // PENDING | PREPARING | READY
    }
}
