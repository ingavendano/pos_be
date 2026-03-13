package com.restaurante.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDto {
    private Long id;
    private Long consecutiveNumber;
    private String status;
    private BigDecimal total;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long tableId;
    private Integer tableNumber;

    private Long branchId;

    private Long userId;
    private String waiterName;

    private CustomerDto customer;
    private InvoiceDto invoice;
    private List<OrderItemDto> items;
}
