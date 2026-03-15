package com.restaurante.backend.domain.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isAvailable;
    private Integer quantity;
    private Integer minStock;
    private String imageUrl;
    private Boolean isSellable;
    private BigDecimal productionCost;
    private Long categoryId;
    private String categoryName;
    private Long tenantId;
}
