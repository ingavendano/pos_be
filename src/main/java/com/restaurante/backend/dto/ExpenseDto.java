package com.restaurante.backend.dto;

import com.restaurante.backend.domain.entity.ExpenseCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDto {
    private Long id;
    private String description;
    private BigDecimal amount;
    private ExpenseCategory category;
    private LocalDate expenseDate;
}
